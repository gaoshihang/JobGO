### 1.Semaphore
synchronized和ReentrantLock只允许一个线程访问某资源，Semaphore（信号量）允许多个线程同时访问某资源。
Semaphore维持了一个可获得许可证的数量，执行acquire方法阻塞，直到有一个许可证可以获取；release方法增加一个许可证，释放一个阻塞的acquire方法。  
**Semaphore经常用于限制可以同时获取某种资源的线程数量。**

### 2.CountDownLatch和CyclicBarrier的区别？
https://mp.weixin.qq.com/s/RLx3QCk-JgNbycaNT8v85A  
（1）CountDownLatch是计数器，只能使用一次；CyclicBarrier的计数器提供reset功能，可以多次使用。   
（2）CountDownLatch是一个异步辅助类，它能让一个和多个线程处于等待状态，直到其他线程完成了一些列操作。比如某个线程需要其他线程执行完毕才能执行其他的。  
（3）CyclicBarrier这个类，让所有的线程都去相互等待，直到它们都到达了一个栏栅的点。通过它可以实现让一组线程等待至某个状态之后再全部同时执行。  
```
public CyclicBarrier(int parties, Runnable barrierAction);

参数parties指让多少个线程或者任务等待至barrier状态；参数barrierAction为当这些线程都达到barrier状态时会执行的内容。
```  
（4）CyclicBarrier类似一个阀门，所有线程都到达后，阀门打开继续执行。  

### 3.CountDownLatch和CyclicBarrier的应用场景
#### CountDownLatch
（1）某线程开始运行前等待n个线程执行完毕；  
（2）启动服务时，主线程需要等待多个组件加载完毕；  
（3）实现多个线程开始执行任务的最大并行性。将多个线程放到起点，等待发号枪响，然后同时开跑；  

#### CyclicBarrier
CyclicBarrier可以用于多线程计算数据，最后合并计算结果的应用场景。




