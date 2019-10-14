### 1.HashMap和ConcurrentHashMap的区别？
HashMap不是线程安全的，ConcurrentHashMap是线程安全的。

ConcurrentHashMap采用锁分段技术，将整个Hash桶进行了分段Segment，也就是将这个大的数组分成了几个小的片段segment，且每个segment上都有
锁存在，在插入元素时候先找到应该插入到哪个segment，然后再在这个片段上进行插入，这样做明显减少了锁的粒度。

### 2.ConcurrentHashMap的实现原理
JDK7：ConcurrentHashMap采用了数组+Segment+分段锁的实现。  
JDK8：ConcurrentHashMap参考了HashMap实现，采用了数组+链表+红黑树的实现方式，内部大量采用CAS操作。

#### （1）JDK1.7
ConcurrentHashMap的主干是Segment数组：
```
final Segment<K, V>[] segments;
```

Segment继承了ReentrantLock，所以它是一种可重入锁。在ConcurrentHashMap中，一个Segment就是一个子哈希表，Segment里维护了一个HashEntry数组，
并发环境下，对于不同Segment的数据进行操作是不用考虑锁竞争的。就按默认的ConcurrentHashMap为16来讲，理论上允许16个线程并发执行。

#### （2）JDK1.8
JDK1.8中Java放弃了Segment臃肿的设计，取而代之采用Node+CAS+Synchronized保证并发更新的安全，底层采用数组+链表+红黑树的存储结构。  
详细请见：https://mp.weixin.qq.com/s/240B5tg_ykwuEJVrOOYNtg
