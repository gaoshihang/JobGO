## 一.理论知识
### 1.Java中线程的生命周期
各种开发语言的线程都是基于操作系统线程。
#### 通用的线程生命周期
五态模型：初始状态、可运行状态、运行状态、休眠状态和终止状态。  
![五态模型](https://upload-images.jianshu.io/upload_images/2818100-69785257bd7dcdb8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

（1）初始状态：线程已经被创建，但是还不能被分配CPU执行。属于编程语言层面，在操作系统层面真正的线程还没有被创建；  
（2）可运行状态：操作系统层面的线程已经创建，可以分配CPU执行；  
（3）运行状态：线程分配到了CPU，进入运行状态；  
（4）休眠状态：运行状态的线程如果调用一个阻塞的 API（例如以阻塞方式读文件）或者等待某个事件（例如条件变量），那么线程的状态就会转换到休眠状态，同时释放 CPU 使用权，休眠状态的线程永远没有机会获得 CPU 使用权。当等待的事件出现了，线程就会从休眠状态转换到可运行状态。  
（5）终止状态：线程执行完或遇到异常。  

#### Java中线程生命周期
Java中共有六种状态，分别是：  
（1）NEW：新建状态  
（2）RUNNABLE：可运行/运行状态  
（3）BLOCKED：阻塞状态  
（4）WAITING：无限时等待状态  
（5）TIMED_WAITING：有时限等待状态  
（6）TERMINATED：终止状态  

**在操作系统层面，BLOCKED、WAITING、TIMED_WATING都是操作系统的休眠状态，也就是说永远没有机会获得CPU使用权**。  
![Java线程状态](https://upload-images.jianshu.io/upload_images/2818100-fb4ec6509d81271c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

##### RUNNABLE与BLOCKED的转换
只有一种场景会触发这种转换，就是线程等待 synchronized 的隐式锁。synchronized 修饰的方法、代码块同一时刻只允许一个线程执行，其他线程只能等待，这种情况下，等待的线程就会从 RUNNABLE 转换到 BLOCKED 状态。而当等待的线程获得 synchronized 隐式锁时，就又会从 BLOCKED 转换到 RUNNABLE 状态。  

##### RUNNABLE与WAITING的转换
总体来说，有三种场景会触发这种转换。  
（1）第一种场景，获得 synchronized 隐式锁的线程，调用无参数的 Object.wait() 方法。  

（2）第二种场景，调用无参数的 Thread.join() 方法。其中的 join() 是一种线程同步方法，例如有一个线程对象 thread A，当调用 A.join() 的时候，执行这条语句的线程会等待 thread A 执行完，而等待中的这个线程，其状态会从 RUNNABLE 转换到 WAITING。当线程 thread A 执行完，原来等待它的线程又会从 WAITING 状态转换到 RUNNABLE。  

（3）第三种场景，调用 LockSupport.park() 方法。其中的 LockSupport 对象，也许你有点陌生，其实 Java 并发包中的锁，都是基于它实现的。调用 LockSupport.park() 方法，当前线程会阻塞，线程的状态会从 RUNNABLE 转换到 WAITING。调用 LockSupport.unpark(Thread thread) 可唤醒目标线程，目标线程的状态又会从 WAITING 状态转换到 RUNNABLE。  

##### RUNNABLE与TIMED_WAITING的转换
有五种场景会触发这种转换：  
（1）调用带超时参数的 Thread.sleep(long millis) 方法；  
（2）获得 synchronized 隐式锁的线程，调用带超时参数的 Object.wait(long timeout) 方法；  
（3）调用带超时参数的 Thread.join(long millis) 方法；  
（4）调用带超时参数的 LockSupport.parkNanos(Object blocker, long deadline) 方法；  
（5）调用带超时参数的 LockSupport.parkUntil(long deadline) 方法。  
这里你会发现 TIMED_WAITING 和 WAITING 状态的区别，仅仅是触发条件多了超时参数。  

##### NEW到RUNNABLE状态
Java刚刚创建出来线程对象就是NEW状态，调用了start方法后进入RUNNABLE状态。  

##### RUNNABLE到TERMINATED状态
线程执行完 run() 方法后，会自动转换到 TERMINATED 状态，当然如果执行 run() 方法的时候异常抛出，也会导致线程终止。  
有时候我们需要强制中断 run() 方法的执行，例如 run() 方法访问一个很慢的网络，我们等不下去了，想终止怎么办呢？Java 的 Thread 类里面倒是有个 stop()方法，不过已经标记为 @Deprecated，所以不建议使用了。正确的姿势其实是调用 interrupt()方法。  

#### 如何诊断多线程BUG？
多线程程序很难调试，出了 Bug 基本上都是靠日志，靠线程 dump 来跟踪问题，分析线程 dump 的一个基本功就是分析线程状态，大部分的死锁、饥饿、活锁问题都需要跟踪分析线程的状态。  
你可以通过 jstack 命令或者Java VisualVM这个可视化工具将 JVM 所有的线程栈信息导出来，完整的线程栈信息不仅包括线程的当前状态、调用栈，还包括了锁的信息。  

### 2.创建多少线程才合适？
#### 为什么要使用多线程？
使用多线程的目的是提升性能，在提升性能之前，需要知道：如何度量性能？  
度量性能最主要的两个指标是延迟和吞吐量。延迟指的是发出请求到收到响应这个过程的时间；吞吐量指的是单位时间内能处理请求的数量。  
所谓提高性能，从度量角度，主要是**降低延迟，提高吞吐量**。  

#### 多线程的应用场景
要想降低延迟、提高吞吐量，对应的方法有两个方向：优化算法和将硬件的性能发挥到极致。  
计算机主要的硬件有两类：一个是I/O，一个是CPU。**在并发编程领域，提升性能本质上就是提升硬件的利用率，就是提升IO利用率和CPU利用率**。  

那么，如何利用多线程来提升CPU和IO设备利用率？  
如果只有一个线程，执行CPU计算时，IO空闲；执行IO操作时，CPU空闲，这时CPU和IO的利用率都是50%。  
![单线程](https://upload-images.jianshu.io/upload_images/2818100-16b48bbc5bbc13e5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
如果有两个线程，A执行CPU时，B执行IO；A执行IO时，B执行CPU，这样CPU和IO利用率都是100%。  
![双线程](https://upload-images.jianshu.io/upload_images/2818100-bb2efc84566625d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
**如果CPU和IO设备利用率都很低，可以尝试通过增加线程来提高吞吐量**。  

#### 创建多少线程合适？
IO设备的速度相对于CPU来说很慢，大部分情况下，IO操作执行时间相对于CPU计算来说都很长。这种场景称为IO密集型计算；  
与之相对的是CPU密集型计算。这两种情形下计算最佳线程数的方法不同：  

**（1）对于CPU密集型计算**：多线程本质上是提升多核CPU的利用率，所以对于一个4core的CPU，理论上创建4个线程即可，多了也是徒增线程切换成本。  
工程上，一般会设置为**CPU cores+1**，这样当线程因为偶尔的内存页失效或其他原因阻塞时，额外的线程可以顶上，保证CPU利用率。  
**（2）对于IO密集型计算**：最佳线程数=CPU核数*[1+（IO耗时/CPU耗时）]。





## 二.一些面试题
### 1.进程和线程的区别
**进程**：**程序运行和资源分配的基本单位**。一个程序至少有一个进程，一个进程至少有一个线程。**进程在执行过程中拥有独立的内存单元，
而多个线程共享内存资源，减少切换次数，从而效率更高。**  
**线程**：**CPU调度和分派的基本单位**，比进程更小的能独立运行的基本单位。同一个进程中的多个线程间可以并发执行。

### 2. 守护线程是什么？
如果想要创建一个线程来执行一些辅助工作，但又不希望这个线程阻碍JVM的关闭，这种情况下需要使用守护线程（Daemon Thread）。  
线程分为两种：普通线程和守护线程。在JVM启动时创建的所有线程中，除了主线程以外，其他的线程都是守护线程（例如垃圾回收器GC）。当创建一个新线程时，新线程将继承创建它的线程的守护状态，因此在默认情况下，主线程创建的所有线程都是普通线程。  
普通线程与守护线程之间的差异仅在于当线程退出时发生的操作。当一个线程退出时，JVM会检查其他正在运行的线程，若其他线程都是守护线程，那么JVM会正常退出操作。
当JVM停止时，所有仍然存在的守护线程都将被抛弃——即不会执行finally代码块，也不会执行回卷栈，而JVM只是直接退出。

### 3.创建线程的几种方式
（1）继承Thread类创建线程；  
（2）实现Runnable接口创建线程；  
（3）通过Callable和Future创建线程；  
（4）通过线程池创建线程。

### 4.Runnable和Callable的区别
（1）Runnable接口中的run()方法返回值是void，只是执行run()中的代码而已。  
（2）Callable接口的call()方法是有返回值的，是一个泛型，可以和Future、FutureTask配合用来获取异步执行的结果。

### 5.线程状态及转换
Thread源码中有6种状态：  
（1）new（新建）  
（2）runnable（可运行）  
（3）blocked（阻塞）  
（4）waiting（等待）  
（5）time waiting（定时等待）  
（6）terminated（终止）
其状态转换图如下所示：  
![微信图片_20191014162703.jpg](https://upload-images.jianshu.io/upload_images/2818100-36bbbac66834460a.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 6.sleep()和wait()的区别
（1）sleep方法使正在执行的线程主动让出cpu（这时cpu可以执行其他任务），在sleep指定时间后cpu回到该线程继续往下执行（**注意：sleep方法只让出cpu，
不会让出同步资源锁**）。wait()方法是指当前线程让自己暂时退让出同步资源锁，以便其他正在等待该资源的线程得以得到该线程继续执行。只有调用了notify()
方法，之前调用wait()的线程才会接触wait()状态，可以参与竞争同步资源锁，进而得到执行。  
（2）sleep方法可以在任何地方使用，wait只能在同步代码块或同步方法中使用。  
（3）sleep是线程类（Thread）的方法，调用会暂停此线程指定的时间，但不会释放对象锁；wait是Object的方法，调用会放弃对象锁，进入等待队列，只有调用
notify()/notifyAll()唤醒指定线程或所有线程，才会进入锁池，再次竞争锁。

### 7.线程的run()和start()有什么区别？
（1）每个线程都是通过特定Thread对象对应的方法run来完成操作的，run称为线程体。通过调用Thread类的start方法来启动一个线程。  
（2）start方法启动一个线程，真正实现多线程运行。这时无需等待run方法执行，就可以直接执行下面的代码；这时，线程是就绪状态，并没有运行。一旦其
得到CPU时间，该线程会调用方法run来完成其运行，run方法中包含了此线程的内容，run方法运行结束，此线程终止。  
（3）run方法只是线程里的一个函数，不是多线程的。如果直接调用run，相当于调用了一个普通函数而已。所以在多线程执行时要使用start而不是run。

### 8.Java程序中怎么保证多线程的运行安全？
线程安全在三个方面体现：  
（1）原子性：提供互斥访问，同一时刻只能有一个线程对数据进行操作（atomic、synchronized）。  
（2）可见性：一个线程对主内存的修改可以及时地被其他线程看到（synchronized、volatile）。  
（3）有序性：一个线程观察其他线程中的指令执行顺序，由于指令重排，该观察结果一般杂乱无章（happens-before原则）。

### 9.Java线程同步的几种方法
（1）synchronized关键字  
（2）wait和notify  
（3）使用特殊域变量volatile实现线程同步  
（4）使用ReentrantLock  
（5）使用阻塞队列  
（6）使用信号量Semaphore




