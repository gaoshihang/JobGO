### 一.全流程
1.源端需要下载OGG for Oracle，目标端需要下载OGG for Big Data。  
2.oracle需要打开归档模式，并打开相关的日志，因为OGG基于辅助日志进行实时传输。  
3.源端OGG配置过程：  
（1）配置管理器mgr；  
（2）添加复制表；  
（3）配置extract进程；  
（4）配置pump进程；  
（5）配置define文件。  
4.目标端OGG配置过程：  
（1）配置管理器mgr；  
（2）配置checkpoint；  
（3）配置replicate进程；  
（4）配置kafka.props;
（5）添加trail文件到replicate进程。  
5.运用多线程从Kafka写入到本地文件。  
6.合并进入Hive

### 二.OGG架构及原理
https://www.jianshu.com/p/415924f86e31  

### 三.Kafka多线程写入本地文件
前一个步骤中，会为每个源端表创建一个topic，这里使用多线程KafkaConsumer方式并行获取每个topic数据。  

Kafka Java Consumer是单线程的设计。  

#### 1.Kafka Java Consumer设计原理
KafkaConsumer这个类是双线程设计，即**用户主线程和心跳线程**。  
用户主线程，是启动Consumer应用main方法的那个线程，而新引入的心跳线程只负责定期给对应的Broker机器发送心跳请求，以标识消费者应用的存活性。**引入这个
心跳线程的目的是，将心跳频率与主线程调用KafkaConsumer.poll方法的频率分开，从而解耦真实的消息处理逻辑与消费者组成员存活性管理**。  

#### 2.多线程方案
KafkaConsumer类不是线程安全的，不能再多个线程中共享同一个KafkaConsumer实例。  
有两套多线程方案：  
（1）消费者程序启动多个线程，每个线程维护专属的KafkaConsumer实例，负责完整的消息获取、消息处理流程。  
（2）消费者使用但或多线程获取消息，同时创建多个消费线程执行消息处理逻辑。处理消息交给特定的线程池来做，从而实现消息获取与处理的解耦。  

### 四.合并进入Hive
https://www.jianshu.com/p/194fbbccada9




