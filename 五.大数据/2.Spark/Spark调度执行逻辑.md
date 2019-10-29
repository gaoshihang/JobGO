https://mp.weixin.qq.com/s/m0imx-bo927UjXxrRiPF4w  
### 一.Spark任务执行流程
（1）用户创建SparkContext，连接到Cluster Manager实例；  
（2）Cluster Manager根据配置的CPU、内存信息、分配资源，启动Executor；  
（3）Driver将程序划分为各个阶段，创建Task，每个阶段包含一组相同Task（分别作用于不同分区）；  
（4）Driver将Task发送到Executor；  
（5）Executor收到Task，下载Task的运行时依赖，准备执行环境；  
（6）Executor执行Task，并将其运行状态汇报给Driver；  
（7）Driver根据Task的运行状态处理更新：  
Shuffle Map Task：数据Shuffle操作（洗牌）  
Result Task：生成数据结果  
（8）Driver不断调用Task，发送给Executor，当所有Task正确执行或超过限制次数后停止。  

### 二.原理分析
RDD以Action算子为界划分Job（作业），每个Job根据依赖关系（宽窄依赖）划分为stage（调度阶段），每个Stage中存在多个Task，组成一个TaskSet（任务集），各个Task可以并发执行，执行逻辑相同，作用于不同数据，处理不同partition（分区）下的数据。**划分工作都是在Driver上进行，Task是被分发到Executor上的任务，是Spark实际执行的基本单元**。  

#### 1.涉及主要类
##### （1）org.apache.spark.scheduler.DAGScheduler
负责分析用户提交应用：  
* 根据依赖关系建立DAG；  
* 将DAG划分到不同Stage；  

##### （2）org.apache.spark.scheduler.TaskScheduler
负责为创建它的SparkContext调度任务：  
* 从DAGScheduler接收Task；  
* 向集群提交这些Task； 
* 为执行慢的task做备份；  

##### （3）org.apache.spark.scheduler.SchedulerBackend
负责分配当前可用资源：  
* 向目前等待分配Executor的Task分配Executor；  
* 在已分配的Executor上启动Task，完成计算调度；  

#### 2.主要流程分析
##### （1）创建SparkContext、DAGScheduler、TaskScheduler

##### （2）提交Job
提交Job按照以下调用步骤：  
* org.apache.spark.SparkContext#runJob  
* org.apache.spark.scheduler.DAGScheduler#runJob  
* org.apache.spark.scheduler.DAGScheduler#submitJob  
submitJob会发生阻塞，直到完成或者返回失败。  

在submitJob中会调用dagScheduler.handleJobSubmitted()，之后就是划分stage了。  

##### （3）划分stage
```
 private[scheduler] def handleJobSubmitted(jobId: Int,
      finalRDD: RDD[_],
      func: (TaskContext, Iterator[_]) => _,
      partitions: Array[Int],
      callSite: CallSite,
      listener: JobListener,
      properties: Properties) {
    var finalStage: ResultStage = null
    try {
      // 获取最后一个Stage
      finalStage = newResultStage(finalRDD, func, partitions, jobId, callSite)
    } catch {
     ......
    }
     // 后面部分代码省略，是下一部分研究的~~
    ......
     // 提交调度，第四部分内容，暂留伏笔
     submitStage(finalStage)
  }
```
该方法首先根据最后一个RDD生成ResultStage，其中newResultStage（）中调用org.apache.spark.scheduler.DAGScheduler#getParentStagesAndId，
进而调用org.apache.spark.scheduler.DAGScheduler#getParentStages获取ParentStage。  

getParentStages()方法就是stage划分阶段重要的逻辑所在了，划分依据就是是否存在shuffle操作。  
主要逻辑就是每遇到一个ShuffleDependency，就生成一个ParentStage。  
```
private def getParentStages(rdd: RDD[_], firstJobId: Int): List[Stage] = {
    // 要返回的 ParentStage
    val parents = new HashSet[Stage]
    // 已被访问过的RDD
    val visited = new HashSet[RDD[_]]
    // 需要被处理的RDD，非ShuffleDependency的RDD
    val waitingForVisit = new Stack[RDD[_]]
  
    waitingForVisit.push(rdd)
    while (waitingForVisit.nonEmpty) {
      visit(waitingForVisit.pop())
    }
    parents.toList
  }
```
其中visit()方法就是遍历处理的方法，先标记访问过的RDD，然后判断当前RDD所依赖的RDD的操作类型，如果是ShuffleDependency，就调用getShuffleMapStage()，划分ShuffleMap调度阶段（向前遍历划分），如果非ShuffleDependency，入waitingForVisit栈中。  
```
 def visit(r: RDD[_]) {
      if (!visited(r)) {
        visited += r
        for (dep <- r.dependencies) {
          dep match {
            case shufDep: ShuffleDependency[_, _, _] =>
              parents += getShuffleMapStage(shufDep, firstJobId)
            case _ =>
              waitingForVisit.push(dep.rdd)
          }
        }
      }
    }
```
**划分调度阶段的方法：org.apache.spark.scheduler.DAGScheduler#getShuffleMapStage**  
主要逻辑是首先寻找该分支上所有宽依赖RDD，生成ShuffleMapStage  
```
private def getShuffleMapStage(
      shuffleDep: ShuffleDependency[_, _, _],
      firstJobId: Int): ShuffleMapStage = {
    shuffleToMapStage.get(shuffleDep.shuffleId) match {
      case Some(stage) => stage
      case None =>
        // 寻找该分支上其他的宽依赖
        getAncestorShuffleDependencies(shuffleDep.rdd).foreach { dep =>
          if (!shuffleToMapStage.contains(dep.shuffleId)) {
            shuffleToMapStage(dep.shuffleId) = newOrUsedShuffleStage(dep, firstJobId)
          }
        }
        // 生成 ShuffleStage
        val stage = newOrUsedShuffleStage(shuffleDep, firstJobId)
        shuffleToMapStage(shuffleDep.shuffleId) = stage
        stage
    }
  }
```
##### （4）提交stage
handleJobSubmitted方法中的submitStage(finalStage)。  
调用getMissingParentStages()获取父stage，如果已经不存在父stage了，就调用 submitMissingTasks(stage, jobId.get)，否则继续递归调用，直到不存在父stage为止。  
```
 private def submitStage(stage: Stage) {
    val jobId = activeJobForStage(stage)
    // 获取finalStage的父stage
    val missing = getMissingParentStages(stage).sortBy(_.id)
    // 不存在父stage
    if (missing.isEmpty) {
        submitMissingTasks(stage, jobId.get)
    }
    else {
        for (parent <- missing) {
           // 递归调用 submitStage
           submitStage(parent)
       }
          waitingStages += stage
     }
  }
```
在提交Stage前，要判断所依赖的父调度阶段（父Stage）是否运行成功，成功才提交该Stage，否则重新提交父Stage。  
判断逻辑在ShuffleMapTask完成时进行，是通过下面的方式完成的：  
在Executor.run()任务执行完成发送消息，通知DAGScheduler等调度器的更新状态，handleTaskCompletion()对事件进行处理。  

##### （5）提交Task
根据调度阶段分区拆分对应个数的Task，组成任务集交给TaskScheduler。  
**主要逻辑**：  
* 对于ShuffleMapStage，生成ShuffleMapTask；  
* 对于ResultStage，生成ResultTask；  

每个TaskSet包含了对应Stage中的所有task，划分依据是数据partition。  
```
 private def submitMissingTasks(stage: Stage, jobId: Int) {
    ......
    val tasks: Seq[Task[_]] = try {
      //1.生成task
      stage match {
        case stage: ShuffleMapStage =>
          partitionsToCompute.map { id =>
            val locs = taskIdToLocations(id)
            val part = stage.rdd.partitions(id)
            new ShuffleMapTask(stage.id, stage.latestInfo.attemptId,
              taskBinary, part, locs, stage.latestInfo.taskMetrics, properties)
          }

        case stage: ResultStage =>
          val job = stage.activeJob.get
          partitionsToCompute.map { id =>
            val p: Int = stage.partitions(id)
            val part = stage.rdd.partitions(p)
            val locs = taskIdToLocations(id)
            new ResultTask(stage.id, stage.latestInfo.attemptId,
              taskBinary, part, locs, id, properties, stage.latestInfo.taskMetrics)
          }
      }
    } catch {
      case NonFatal(e) =>
        abortStage(stage, s"Task creation failed: $e\n${Utils.exceptionString(e)}", Some(e))
        runningStages -= stage
        return
    }
    // 提交
    if (tasks.size > 0) {
     
      stage.pendingPartitions ++= tasks.map(_.partitionId)
      //2.提交task
      taskScheduler.submitTasks(new TaskSet(
        tasks.toArray, stage.id, stage.latestInfo.attemptId, jobId, properties))
      stage.latestInfo.submissionTime = Some(clock.getTimeMillis())
    } else {
     
      markStageAsFinished(stage, None)
    }
  }
```

提交task步骤如下：  
* 创建TaskSetManager；  
* 将该TaskSetManager加入到系统资源调度池，系统统一调配，支持FIFO和FAIR；  
* 执行调度器后台进程SparkDeploySchedulerBackend的reviveOffers()方法，分配资源；  
* 向DriverEndPoint终端点发消息，调用org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend.DriverEndpoint#makeOffers：  
  * 获取集群中可用的Executor；  
  * 发到TaskSchudlerImpl分配资源；  
  * 提交到launchTasks；  
  
```
 override def submitTasks(taskSet: TaskSet) {
    val tasks = taskSet.tasks
   
    this.synchronized {
        // 创建任务集管理器
      val manager = createTaskSetManager(taskSet, maxTaskFailures)
      val stage = taskSet.stageId
      val stageTaskSets =
        taskSetsByStageIdAndAttempt.getOrElseUpdate(stage, new HashMap[Int, TaskSetManager])
      stageTaskSets(taskSet.stageAttemptId) = manager
      val conflictingTaskSet = stageTaskSets.exists { case (_, ts) =>
        ts.taskSet != taskSet && !ts.isZombie
      }
     
       
      schedulableBuilder.addTaskSetManager(manager, manager.taskSet.properties)
      ......
      backend.reviveOffers()
  }
  
 private def makeOffers() {
    
      val activeExecutors = executorDataMap.filterKeys(executorIsAlive)
      val workOffers = activeExecutors.map { case (id, executorData) =>
        new WorkerOffer(id, executorData.executorHost, executorData.freeCores)
      }.toSeq
      launchTasks(scheduler.resourceOffers(workOffers))
    }  
```

##### （6）执行Task
org.apache.spark.scheduler.cluster.CoarseGrainedSchedulerBackend接收到LaunchTask的消息，会调用org.apache.spark.executor.Executor#launchTask，初始化一个TaskRunner，放到线程池中执行。  
```
def launchTask(
      context: ExecutorBackend,
      taskId: Long,
      attemptNumber: Int,
      taskName: String,
      serializedTask: ByteBuffer): Unit = {
    val tr = new TaskRunner(context, taskId = taskId, attemptNumber = attemptNumber, taskName,
      serializedTask)
    runningTasks.put(taskId, tr)
    threadPool.execute(tr)
  }
```
org.apache.spark.executor.Executor.TaskRunner#run省略了一些代码，包括反序列化Task以及Task所依赖的jar文件:  
```
 override def run(): Unit = {
     ......
       var taskStart = System.currentTimeMillis()
        val value = try {
          val res = task.run(
            taskAttemptId = taskId,
            attemptNumber = attemptNumber,
            metricsSystem = env.metricsSystem)
          threwException = false
          res
        } finally {
        ......
        }
        val taskFinish = System.currentTimeMillis()
    }
```
然后会调用org.apache.spark.scheduler.Task#runTask方法，由于Task是一个抽象类，有两个实现类  
org.apache.spark.scheduler.ShuffleMapTask  
org.apache.spark.scheduler.ResultTask  

对于ResultTask，计算结果会直接返回；  
对于ShuffleMapTask，计算结果写入BlockManager中，返回一个MapStatus对象，这个对象存储的是结果存入BlockManager的相关信息，这样做是为了方便下一阶段任务获得输入数据。  

















