对于MapReduce计算框架，有两个关键问题：  
（1）如何为每个数据块分配一个Map计算任务，也就是代码是如何发送到数据块所在服务器的，发送后是如何启动的，启动以后如何知道自己需要计算的数据在文件什么位置（BlockID是什么）。  
（2）处于不同服务器的map输出的<Key, Value> ，如何把相同的Key聚合在一起发送给Reduce任务进行处理。  

这两个关键问题对应于MapReduce计算过程的步骤如下所示：  
![MapReduce计算过程](https://static001.geekbang.org/resource/image/f3/9c/f3a2faf9327fe3f086ec2c7eb4cd229c.png)  

### 一.MapReduce作业启动和运行机制
#### 以Hadoop 1为例，MapReduce运行过程涉及三类关键进程
（1）大数据应用进程。这类进程是启动MapReduce程序的主入口，主要是指定Map和Reduce类、输入输出文件路径等，并提交作业给Hadoop集群，也就是下面提到的JobTracker进程。这是由用户启动的MapReduce程序进程。  
（2）JobTracker。这类进程根据要处理的输入数据量，命令下面提到的TaskTracker进程启动相应数量的Map和Reduce进程任务，并管理整个作业生命周期的任务调度和监控。这是Hadoop集群的常驻进程，需要注意的是，JobTracker进程在整个Hadoop集群全局唯一。  
（3）TaskTracker。这个进程负责启动和管理Map进程以及Reduce进程。因为需要每个数据块都有对应的map函数，TaskTracker进程通常和HDFS的DataNode进程启动在同一个服务器。也就是说，Hadoop集群中绝大多数服务器同时运行DataNode进程和TaskTracker进程。  

JobTracker和TaskTracker是主从关系，主服务器通常只有一台（可能有备用服务器，但对外服务的只有一台），主服务器负责为应用程序分配服务器资源以及作业执行的调度，而具体的计算操作则在从服务器上完成。  

以下是MapReduce计算过程的图示：  
![MapReduce计算过程](https://static001.geekbang.org/resource/image/2d/27/2df4e1976fd8a6ac4a46047d85261027.png)  
下面详述步骤：  
1.JobClient将作业需要的jar包上传到hdfs，将来这些jar包会分发给MapReduce服务器；  
2.JobClient将作业提交给JobTracker；  
3.JobTracker根据作业调度策略创建JobInProcess树，每个作业都有自己的JobInProcess树；  
4.JobInProcess根据输入数据分片的多少以及设置的reduce数量创建相应数量的TaskInProcess；  
5.TaskTracker定期与JobTracker进行心跳通信；  
6.如果TaskTracker有空闲的计算资源（有空闲CPU核心），JobTracker就会给它分配任务。分配任务的时候会根据TaskTracker的服务器名字匹配在同一台机器上的数据块计算任务给它，使启动的计算任务正好处理本机上的数据，以实现我们一开始就提到的“移动计算比移动数据更划算”；    
7.TaskTracker收到任务后根据任务类型（是Map还是Reduce）和任务参数（作业JAR包路径、输入数据文件路径、要处理的数据在文件中的起始位置和偏移量、数据块多个备份的DataNode主机名等），启动相应的Map或者Reduce进程；  
8.Map或者Reduce进程启动后，检查本地是否有要执行任务的JAR包文件，如果没有，就去HDFS上下载，然后加载Map或者Reduce代码开始执行；  
9.如果是Map进程，从HDFS读取数据（通常要读取的数据块正好存储在本机）；如果是Reduce进程，将结果数据写出到HDFS。  

### 二.MapReduce数据合并与连接机制
**MapReduce计算真正产生奇迹的地方是数据的合并与连接。**  
这个操作有个专门的词汇，称为**shuffle**，其具体过程如下图所示：  
![Shuffle](https://static001.geekbang.org/resource/image/d6/c7/d64daa9a621c1d423d4a1c13054396c7.png)  
每个Map任务的计算结果都会写入本地文件系统，等Map任务快要计算完成的时候，MapReduce计算框架会启动shuffle过程，在Map任务进程调用一个Partitioner接口，
对Map产生的每个<Key, Value>进行Reduce分区选择，然后通过HTTP通信发送给对应的Reduce进程。Reduce任务对收到的<Key, Value>进行排序和合并，相同的Key放在
一起，组成一个<Key, Value集合>传递给Reduce执行。  

**map输出的<Key, Value>shuffle到哪个Reduce进程是这里的关键，它是由Partitioner来实现，MapReduce框架默认的Partitioner用Key的哈希值对Reduce任务数量取模，相同的Key一定会落在相同的Reduce任务ID上。**  





