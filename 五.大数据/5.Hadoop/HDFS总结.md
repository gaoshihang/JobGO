### 一.HDFS架构
![hdfs架构](https://static001.geekbang.org/resource/image/65/d7/65efd126cbcf3930a706f64c6e6457d7.jpg)  

HDFS的关键组件有两个，NameNode和DataNode。  
**DataNode负责文件数据的存储和读写操作，HDFS将文件数据分割成若干数据块（Block），每个DataNode存储一部分数据块，这样文件就分布存储在整个HDFS服务器集群中。**  
应用程序客户端（Client）可以并行对这些数据块进行访问，从而使得HDFS可以在服务器集群规模上实现数据并行访问，极大地提高了访问速度。  
**NameNode负责整个分布式文件系统的元数据（MetaData）管理，也就是文件路径名、数据块的ID以及存储位置等信息，相当于操作系统中文件分配表（FAT）的角色。**  
HDFS为了保证数据的高可用，会将一个数据块复制为多份（缺省情况为3份），并将多份相同的数据块存储在不同的服务器上，甚至不同的机架上。这样当有磁盘损坏，或者某个DataNode服务器宕机，甚至某个交换机宕机，导致其存储的数据块不能访问的时候，客户端会查找其备份的数据块进行访问。  

下图是数据块多份复制存储的示例：  
![HDFS数据块](https://static001.geekbang.org/resource/image/6f/ac/6f2faa48524251ad77e55e3565095bac.jpg)  

### 二.HDFS如何保证高可用？  
以下从不同层面讨论HDFS的高可用性。  
#### 1.数据存储故障
磁盘介质在存储过程中受环境或者老化影响，其存储的数据可能会出现错乱。HDFS的应对措施是，对于存储在DataNode上的数据块，计算并存储校验和（CheckSum）。在读取数据的时候，重新计算读取出来的数据的校验和，如果校验不正确就抛出异常，应用程序捕获异常后就到其他DataNode上读取备份数据。  

#### 2.磁盘故障容错
如果DataNode监测到本机的某块磁盘损坏，就将该块磁盘上存储的所有BlockID报告给NameNode，NameNode检查这些数据块还在哪些DataNode上有备份，通知相应的DataNode服务器将对应的数据块复制到其他服务器上，以保证数据块的备份数满足要求。  

#### 3.DataNode故障容错
DataNode会通过心跳和NameNode保持通信，如果DataNode超时未发送心跳，NameNode就会认为这个DataNode已经宕机失效，立即查找这个DataNode上存储的数据块有哪些，以及这些数据块还存储在哪些服务器上，
随后通知这些服务器再复制一份数据块到其他服务器上，保证HDFS存储的数据块备份数符合用户设置的数目，即使再出现服务器宕机，也不会丢失数据。  

#### 4.NameNode故障容错
NameNode是整个HDFS的核心，记录着HDFS文件分配表信息，所有的文件路径和数据块存储信息都保存在NameNode，如果NameNode故障，整个HDFS系统集群都无法使用；
如果NameNode上记录的数据丢失，整个集群所有DataNode存储的数据也就没用了。  

**NameNode采用主从热备方式实现高可用**，如下图所示：  
![NameNode高可用](https://static001.geekbang.org/resource/image/7c/89/7cb2668644c32364beab0b69e60b3689.png)  

集群部署两台NameNode服务器，一台作为主服务器提供服务，一台作为从服务器进行热备，两台服务器通过ZooKeeper选举，主要是通过争夺znode锁资源，决定谁是主服务器。
而DataNode则会向两个NameNode同时发送心跳数据，但是只有主NameNode才能向DataNode返回控制信息。  
正常运行期间，主从NameNode之间通过一个共享存储系统shared edits来同步文件系统的元数据信息。当主NameNode服务器宕机，从NameNode会通过ZooKeeper升级成为主服务器，
并保证HDFS集群的元数据信息，也就是文件分配表信息完整一致。  


