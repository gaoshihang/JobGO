https://mp.weixin.qq.com/s/rPAzmN14joF5lOSZ11H28Q
#### 1.RDD、DAG、Stage怎么理解
（1）RDD  
弹性分布式数据集，一个RDD代表一个可以被分区的只读数据集。RDD 内部可以有许多分区(partitions)，每个分区又拥有大量的记录(records)。  
两种算子：Action和Transformation  
（2）DAG  
有向无环图，描述RDD的依赖关系，也称为血缘，其对应实现为DAGScheduler。  
（3）Stage  
DAG会根据是否shuffle划分为多个Stage，每个Stage包含多个Task，Task由Driver发送给Executor去执行。Action算子是用来划分job的。  

#### 2.宽依赖、窄依赖
窄依赖：一子一父。在同一节点上处理。  
宽依赖：多子一父。需要跨节点。  

#### 3.Job和Task的理解
Job：通过action算子划分。包含很多并行task，提交的job会给到DAGScheduler，分解为Stage和Task。  
Task：一个Stage内，有多少个partition，就有多少task。  

#### 4.Spark血统概念
记录的是粗粒度的transformation操作行为。当某分区数据丢失时，可以通过血统由父分区重新计算。  

#### 5.Action和Transformation
Transformation操作从一个RDD创建一个新的RDD，Action触发真正计算，并返回结果给Driver。  
Transformation有map、filter等，Action有count等。  

#### 6.Spark提交作业流程
（1）spark-submit ---> new SparkContext ---> 在SparkContext里构造DAGScheduler和TaskScheduler；  
（2）TaskScheduler向Master注册Application；  
（3）Master在Worker上启动多个Executor；  
（4）每执行到一个Action，创建一个Job，提交给DAGScheduler；  
（5）DAGScheduler将Job划分为多个Stage，每个Stage创建一个TaskSet；  
（6）TaskScheduler将TaskSet里的作业分布到Executor上执行；  

#### 7.Storm与Spark Streaming的区别
![区别](https://mmbiz.qpic.cn/mmbiz_png/UdK9ByfMT2OSwS8tHQeMicc0egREicTZ5ReicfWZQEnOicP6cB9Co4Z6SXrEAsqwsKbAY9aSRORx2Ofs9iaApOxnCcg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)  

#### 8.为什么要用YARN部署Spark？  
YARN支持动态资源配置，适合多用户情景。  

#### 9.YARN-CLUSTER和YARN-CLIENT的异同点
cluster 模式会在集群的某个节点上为 Spark 程序启动一个称为 Master 的进程，然后 Driver 程序会运行正在这个 Master 进程内部，由这种进程来启动 Driver 程序，客户端完成提交的步骤后就可以退出，不需要等待 Spark 程序运行结束，这是适合生产环境的运行方式。  
client 模式也有一个 Master 进程，但是 Driver 程序不会运行在这个 Master 进程内部，而是运行在本地，只是通过 Master 来申请资源，直到运行结束，这种模式非常适合需要交互的计算。显然 Driver 在 client 模式下会对本地资源造成一定的压力。  

#### 10.解释一下 groupByKey, reduceByKey
groupByKey该函数用于将RDD[K,V]中每个K对应的V值，合并到一个集合Iterable[V]中：  
```
scala> var rdd1 = sc.makeRDD(Array(("A",0),("A",2),("B",1),("B",2),("C",1)))
rdd1: org.apache.spark.rdd.RDD[(String, Int)] = ParallelCollectionRDD[89] at makeRDD at :21
 
scala> rdd1.groupByKey().collect
res81: Array[(String, Iterable[Int])] = Array((A,CompactBuffer(0, 2)), (B,CompactBuffer(2, 1)), (C,CompactBuffer(1)))
```

reduceByKey该函数用于将RDD[K,V]中每个K对应的V值根据映射函数来运算:  
```
scala> var rdd1 = sc.makeRDD(Array(("A",0),("A",2),("B",1),("B",2),("C",1)))
rdd1: org.apache.spark.rdd.RDD[(String, Int)] = ParallelCollectionRDD[91] at makeRDD at :21
 
scala> rdd1.partitions.size
res82: Int = 15
 
scala> var rdd2 = rdd1.reduceByKey((x,y) => x + y)
rdd2: org.apache.spark.rdd.RDD[(String, Int)] = ShuffledRDD[94] at reduceByKey at :23
 
scala> rdd2.collect
res85: Array[(String, Int)] = Array((A,2), (B,3), (C,1))
```

#### 11.cache和persist的区别
cache()是persist()的简化方式，调用persist的无参版本，也就是调用persist(StorageLevel.MEMORY_ONLY)，cache只有一个默认的缓存级别MEMORY_ONLY，即将数据持久化到内存中，而persist可以通过传递一个 StorageLevel 对象来设置缓存的存储级别。






















