一般来说，分布式数据集的容错方式有两种：数据检查点和记录数据更新。  
面向大规模数据分析，数据检查点操作成本很高，需要通过数据中心的网络连接在机器之间复制庞大的数据集，而网络带宽往往比内存带宽低得多，同时还需要消耗更多的存储资源。  

因此，**Spark选择记录更新的方式**。但是，如果更新粒度太细太多，那么其成本也不低。因此，RDD只支持粗粒度转换，即只记录单个块上执行的单个操作，然后将
创建RDD的一系列变换序列（每个RDD都包含它是如何由其他RDD变换来的，以及如何重建某一块数据的信息。因此RDD的容错机制又叫“血统（Lineage）容错”）记录
下来，以便恢复丢失的分区。  

**Lineage本质上类似数据库中的redo log，只不过这个重做日志粒度很大，是对全局数据做同样的重做进而恢复数据**。  

### 一.Lineage机制
相比其他系统的细颗粒度的内存数据更新级别的备份或者LOG机制，RDD的Lineage记录的是粗颗粒度的特定数据Transformation操作（如filter、map、join等）行为。
当这个RDD的部分分区数据丢失时，它可以通过Lineage获取足够的信息来重新运算和恢复丢失的数据分区。
因为这种粗颗粒的数据模型，限制了Spark的运用场合，所以Spark并不适用于所有高性能要求的场景，但同时相比细颗粒度的数据模型，也带来了性能的提升。  

#### 两种依赖关系
##### （1）窄依赖
是指父RDD的每一个分区最多被一个子RDD的分区所用。有两种表现：  
* 一个父RDD的分区对应于一个子RDD的分区（map，filter算子）；  
* 多个父RDD的分区对应于一个子RDD的分区（join）；  

##### （2）宽依赖
指子RDD的分区依赖于父RDD的多个分区或所有分区，即存在一个父RDD分区对应一个子RDD的多个分区。  

**本质区分：根据父RDD是对应1个还是多个子RDD分区来区分窄依赖和宽依赖**。  

#### 依赖关系特性
（1）窄依赖可以在某个计算节点上直接通过计算父RDD的某块数据计算得到子RDD对应的某块数据；宽依赖必须要等到所有父RDD的数据都计算完成后，且其计算结果
进行hash传到对应节点上才能计算子RDD。  
（2）数据丢失时，窄依赖只需要计算丢失的那一块数据即可恢复；而对于宽依赖，需要将祖先RDD中的所有数据块全部重新计算来恢复，所以，**需要在适当的时机
设置数据检查点（CheckPoint）**。也是这两个特性要求对于不同依赖关系要采用不同的容错恢复机制。  

#### 容错原理
在容错机制中，如果一个节点死机了，而且运算窄依赖，则只要把丢失的父RDD分区重算即可，不依赖于其他节点。
而宽依赖需要父RDD的所有分区都存在，重算就很昂贵了。可以这样理解开销的经济与否：  
在窄依赖中，在子RDD的分区丢失、重算父RDD分区时，父RDD相应分区的所有数据都是子RDD分区的数据，并不存在冗余计算。  
在宽依赖情况下，丢失一个子RDD分区重算的每个父RDD的每个分区的所有数据并不是都给丢失的子RDD分区用的，会有一部分数据相当于对应的是未丢失的子RDD分区中需要的数据，
这样就会产生冗余计算开销，这也是宽依赖开销更大的原因。  
**因此如果使用Checkpoint算子来做检查点，不仅要考虑Lineage是否足够长，也要考虑是否有宽依赖，对宽依赖加Checkpoint是最物有所值的。**  

### 二.CheckPoint机制  
从上述分析可以看出，在以下两种情况下，RDD需要加检查点：  
* DAG中的Lineage过长，如果重算，则开销太大（如在PageRank中）。  
* 在宽依赖上做Checkpoint获得的收益更大。  

在RDD计算中，通过检查点机制进行容错，传统做检查点有两种方式：
**通过冗余数据和日志记录更新操作。在RDD中的doCheckPoint方法相当于通过冗余数据来缓存数据，而之前介绍的血统就是通过相当粗粒度的记录更新操作来实现容错的。**  

**检查点（本质是通过将RDD写入Disk做检查点）是为了通过lineage做容错的辅助，lineage过长会造成容错成本过高，这样就不如在中间阶段做检查点容错，
如果之后有节点出现问题而丢失分区，从做检查点的RDD开始重做Lineage，就会减少开销。**  

#### 1.如何使用checkpoint？
启用 checkpoint，需要设置一个支持容错 的、可靠的文件系统（如 HDFS、s3 等）目录来保存 checkpoint 数据。
通过调用 streamingContext.checkpoint(checkpointDirectory) 来完成。  

#### 2.导出checkpoint的时机
两个问题：  
* 在什么时机进行checkpoint？  
* checkpoint的形式是什么样的？  

##### （1）checkpoint的时机
在 Spark Streaming 中，JobGenerator 用于生成每个 batch 对应的 jobs，它有一个定时器，定时器的周期即初始化 StreamingContext 时设置的 batchDuration。
这个周期一到，JobGenerator 将调用generateJobs方法来生成并提交 jobs，这之后调用 doCheckpoint 方法来进行 checkpoint。
doCheckpoint 方法中，会判断当前时间与 streaming application start 的时间之差是否是 checkpoint duration 的倍数，只有在是的情况下才进行 checkpoint。

##### （2）checkpoint的形式
checkpoint 的形式是将类 Checkpoint的实例序列化后写入外部存储，类Checkpoint包含以下数据：  
![checkpoint内容](https://mmbiz.qpic.cn/mmbiz_jpg/UdK9ByfMT2OSwS8tHQeMicc0egREicTZ5Rh6v7QXQpXThgiabJmFqILzAV0QQac83ricDRZDlqOh4iaZBQCmPdEicS4Q/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)  











