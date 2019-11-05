在计算机数据存储领域，一直是RDBMS的天下，在传统企业的应用领域，许多应用系统设计都是面向数据库设计，也就是**先设计数据库然后设计程序**，从而导致
**关系模型绑架对象模型**。  
关系数据库有着难以克服的缺陷——糟糕的海量数据处理能力及僵硬的设计约束。所以，为了解决这些问题，NoSQL概念被提出。  
**NoSQL，主要指非关系的、分布式的、支持海量数据存储的数据库设计模式**。其中，HBase是这类NoSQL的杰出代表。  
HBase之所以能够处理海量数据，其根本在于和传统RDBMS设计具有不同思路。RDBMS对存储其上的数据有很多约束，学习时都要学习数据库设计范式，事实上，是在数据
存储中包含了一部分业务逻辑。而NoSQL则简单的认为，数据库就是存储数据的，业务逻辑应该由应用程序去处理。  

### 一.Hbase可伸缩架构
HBase为可伸缩海量数据存储设计，实现面向在线业务的实时数据访问延迟。**HBase的伸缩性主要依赖其可分裂的HRegion以及可伸缩的分布式文件系统HDFS实现**。  

下图为HBase的架构：  
![Hbase架构](https://static001.geekbang.org/resource/image/9f/f7/9f4220274ef0a6bcf253e8d012a6d4f7.png)  
**HRegion是负责存储数据的主要进程，应用程序对数据的读写都是通过与HRegion通信完成**。应用程序如果想要访问一个数据，必须先找到HRegion，将数据读写操作
提交给HRegion，由HRegion完成存储层面的数据操作。  
**HRegionServer是物理服务器，每个HRegionServer上可以启动多个HRegion实例**。当一个HRegion过大时，达到了设置的阈值，会分裂成两个HRegion，HRegion会
在整个集群里进行迁移，使得HRegionServer负载均衡。  
**每个HRegion中存储的是一段key值区间[key1, key2)的数据，所有HRegion的信息，如存储的key值区间、所在HRegionServer地址、访问端口等，都记录在HMaster上。
**为了保证HMaster的高可用，HBase会启动多个HMaster，并通过Zookeeper选举一个主服务器。  

HBase的调用时序图如下：  
![HBase调用时序图](https://static001.geekbang.org/resource/image/9f/ab/9fd982205b06ecd43053202da2ae08ab.png)  
应用程序先请求zookeeper，获取主HMaster地址，然后将key值输入，获取这个key所在的HRegionServer地址，最后请求HRegionServer上对应的HRegion，获取所需数据。  
数据的写入过程也类似，需要先获得HRegion才能继续操作。**HRegion会把数据存储在若干个HFile格式的文件中，HFile文件使用HDFS进行存储，在整个集群中分布且
高可用**。当一个HRegion中的数据量太多时，HRegion连同其存储的HFile会分裂成两个HRegion，并根据集群当前的负载进行迁移。如果集群中新加入了服务器，因为
其负载较低，这时也会发生HRegion的迁移。  



