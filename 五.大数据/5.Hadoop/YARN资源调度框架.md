**YARN的出现使Hadoop从一个单一的大数据计算引擎，成为一个集存储、计算、资源管理为一体的完整大数据平台。**  

### 1.Hadoop 1中资源调度方案的缺点
在Hadoop 1中，MapReduce程序的运行主要由JobTracker和TaskTracker来完成。这种架构方案的缺点是：**服务器集群资源调度管理和MapReduce执行过程耦合在一起，如果想在当前集群中运行其他计算任务，比如Spark或者Storm，就无法统一使用集群中的资源了。**  
所以，Hadoop 2中最主要的变化就是**将YARN从MapReduce中分离出来，成为一个独立的资源调度框架。**  

### 2.YARN架构
下图为YARN的架构图：  
![YARN架构图](https://static001.geekbang.org/resource/image/af/b1/af90905013e5869f598c163c09d718b1.jpg)  
Yarn包括两个部分：一个是资源管理器（Resource Manager），一个是节点管理器（Node Manager）。这也是Yarn的两种主要进程：ResourceManager进程负责整个集群的资源调度管理，通常部署在独立的服务器上；
NodeManager进程负责具体服务器上的资源和任务管理，在集群的每一台计算服务器上都会启动，基本上跟HDFS的DataNode进程一起出现。  

具体来说，**ResourceManager又包含两个主要组件：调度器和应用程序管理器。**  
**调度器其实就是一个资源分配算法**，根据应用程序（Client）提交的资源申请和当前服务器集群的资源状况进行资源分配。Yarn内置了几种资源调度算法，包括Fair Scheduler、Capacity Scheduler等，你也可以开发自己的资源调度算法供Yarn调用。  
**Yarn进行资源分配的单位是容器（Container）**，每个容器包含了一定量的内存、CPU等计算资源，默认配置下，每个容器包含一个CPU核心。容器由NodeManager进程启动和管理，NodeManger进程会监控本节点上容器的运行状况并向ResourceManger进程汇报。  
**应用程序管理器负责应用程序的提交、监控应用程序运行状态等**。应用程序启动后需要在集群中运行一个ApplicationMaster，ApplicationMaster也需要运行在容器里面。
每个应用程序启动后都会先启动自己的ApplicationMaster，由ApplicationMaster根据应用程序的资源需求进一步向ResourceManager进程申请容器资源，得到容器以后就会分发自己的应用程序代码到容器上启动，进而开始分布式计算。  

下面来看一个MapReduce在YARN下运行的工作流程：  
（1）向YARN提交程序，包括MapReduce ApplicationMaster、MapReduce程序以及MapReduce Application启动命令；  
（2）ResourceManager进程和NodeManager进程通信，根据集群资源，为用户程序分配第一个容器，并将ApplicationMaster分发到这个容器上，在容器中启动ApplicationMaster；  
（3）ApplicationMaster启动后立即向ResourceManager注册，并为自己的应用程序申请容器资源；  
（4）申请到需要容器后，和相应的NodeManager进程通信，将用户MapReduce程序分发到NodeManager进程所在服务器，并在容器中运行，运行的是map或者reduce任务；  
（5）Map或者Reduce任务在运行期和MapReduce ApplicationMaster通信，汇报自己的运行状态，如果运行结束，MapReduce ApplicationMaster向ResourceManager进程注销并释放所有的容器资源；  
