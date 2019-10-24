Kafka通过其partition和replica机制来保证高可用。  

Kafka集群是由多个broker组成的，每个broker是一个节点。一个topic被划分为多个partition，每个partition存放一部分数据，存储在不同的broker上，这就是
天然的分布式消息队列。  
但是Kafka在0.8之前，是没有HA机制的，也就是说，如果某个broker宕机了，其上的partition数据就会丢失。  
0.8以后，Kafka提供了HA机制，也就是replica副本机制。每个partition会有多个replica副本，分布在不同的机器上。这些replica副本会推举一个leader出来，数据
的读写都是跟这个leader打交道。在写的时候，leader负责将数据同步到所有follower上面，读的时候就直接读leader上的数据。  

