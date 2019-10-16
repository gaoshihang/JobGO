### 1.什么是AQS？
AbstractQueuedSynchronizer，在包java.util.concurrent.locks下。AQS是一个用来构建锁和同步器的框架，比如ReentrantLock、Semaphore等，都是基于AQS。

### 2.AQS的原理
AQS核心思想：如果被请求的共享资源空闲，则将当前请求线程设置为有效的工作线程，且将该共享资源设置为锁定状态。如果该资源被占用，需要一套线程阻塞等待
以及被唤醒时锁分配的机制，AQS使用了CLH队列，将暂时获取不到锁的线程加入队列。

**什么是CLH队列？**
CLH队列是一个虚拟双向队列（不存在队列实例，仅存在结点之间关联关系）。AQS将每条请求共享资源的线程封装成一个CLH锁队列的一个Node来实现锁分配。

### 3.AQS对资源的共享模式
（1）独占：只有一个线程能执行，如ReentrantLock。  
（2）共享：多个线程可同时执行，如：CountDownLatch、Semaphore。

### 4.参考资料
https://mp.weixin.qq.com/s/zdn54VeNSsabwDd3CBvSoA
