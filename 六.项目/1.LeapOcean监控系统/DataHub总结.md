DataHub支持数据无缝传输和迁移，支持本地文件、数据库、第三方云平台等多种源类型的数据迁移，具体支持类型如下表：  
![DataHub](https://upload-images.jianshu.io/upload_images/2818100-f3e063e29814a5ba.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
### 一.架构
![DataHub架构](https://upload-images.jianshu.io/upload_images/2818100-c18dbfde75dcdf46.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

### 二.技术点
#### 1.数据传输
在进行数据传输时，会根据选择源表的数量创建对应的CountDownLatch，开启多线程并行的进行多表的传输，如果CountDownLatch不为0，则令主线程等待。  

系统中会设置两个参数：batchSize和SqoopSize。在传输开始前会对要传输的表进行“select count”查询，获取到表的行数。  
如果该行数大于batchSize，会判断是否支持分块传输,如果支持，会使用并发的方式上传；如果该行数大于sqoopSize，会使用Sqoop进行传输；如果前两个条件都不满足，则使用普通流程传输。  
![不同策略处理](https://upload-images.jianshu.io/upload_images/2818100-36f9cd7141bd5be5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 分块传输
分块时，需要计算总块数，通过total和batchSize两个参数进行计算：  
如果total < batchSize，则分块数为1；如果total % batchSize == 0，则分块数为total / batchSize；如果total % batchSize != 0，则分块数为
total / batchSize + 1。  

1.处理时，先将分块表输出到本地文件。  
先获取分区字段，如果有主键，则以主键作为分区字段；如果不存在主键，则使用可排序的字段作为分区字段。  
获取分区字段的最大值和最小值，这里输入一个查询语句（select min() miv, max() mav from ...）。  
构造一个TablePartition的list，根据max-min/block数量，构建TablePartition，每个TableParition包含自己的sql语句。  

2.然后，将本地文件输出至HDFS。  

##### Sqoop传输
这里使用Sqoop2。先创建SqoopClient，然后挑选分区字段，默认为第一列，如果有主键的话则选择主键作为分区字段。构建Sqoop2任务，使用SqoopClient.start进行任务启动。  

###### Sqoop面试题
1.什么是Sqoop？  
Sqoop是用来在RDBMS和Hadoop间进行高效的大数据传输的工具。其底层使用MapReduce进行ETL，保证了并行化和高容错率。  

2.Sqoop 1和Sqoop 2的对比  
（1）架构上，Sqoop1只有一个客户端；而Sqoop2引入了Sqoop Server，对Connector实现了集中管理，其访问方式也变得多样化，可以通过REST API、**JAVA API**以及WEB UI进行访问。  
（2）Sqoop2的安全性较高。  

3.Sqoop2数据导入  
将数据从RDBMS导入到Hadoopp中：  
Step 1，Sqoop与数据库Server通信，获取数据库表的元数据信息；  
Step 2，Sqoop启动一个Map-Only的ME作业，利用元数据信息并行将数据写入Hadoop。  

Sqoop在import时，要指定split-by参数，Sqoop对其进行切分后分配到不同的map中。

#### 2.任务调度（Quartz）
这里使用Spring-Quartz来作为任务的调度中心，在使用时，要创建许多quartz默认的表，比如：  
QRTZ_JOB_DETAILS：存储Job信息表  
QRTZ_TRIGGERS：存储Trigger信息  
QRTZ_CRON_TRIGGERS：存储Cron Trigger，包括Cron表达式和时区信息。  
等等。。。。。。  

**SchedulerFactoryBean**这个类的真正作用提供了对org.quartz.Scheduler的创建与配置，并且会管理它的生命周期与Spring同步。  
**org.quartz.Scheduler**: 调度器。所有的调度都是由它控制。  

其中，使用JobManager包装了Scheduler，所有的任务调度都在其中进行。  
新建任务后，会创建一个线程，从线程池中调度一个线程进行执行。创建线程池使用方式为Executors.newFixedThreadPool(jobSubThreadSize);  

如果是一次性任务，调用executeOnce；如果是周期性任务，则调用addJob添加进入调度中心。  

##### 一些面试题
###### 包含的组件
1.Job  
表示一个任务（工作），要执行的具体内容。  

2.JobDetail  
表示一个具体的可执行的调度程序，Job 是这个可执行程调度程序所要执行的内容，另外JobDetail还包含了这个任务调度的方案和策略。  

3.Trigger  
代表一个调度参数的配置，什么时候去调。  

4.Scheduler  
调度中心。代表一个调度容器，一个调度容器中可以注册多个 JobDetail 和 Trigger。当 Trigger 与JobDetail组合，就可以被Scheduler容器调度了，  



