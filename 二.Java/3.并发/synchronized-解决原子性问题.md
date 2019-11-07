一个或多个操作在CPU执行过程中不被中断的特性，称为“原子性”。  

### 1.如何解决原子性问题？
long型变量在32位机器上读写会出现诡异bug：把变量成功写入内存，重新读出来却不是自己写入的。  
long型变量是64位的，在32位CPU上执行写操作会被拆分为两次写操作（写高32位和写低32位），如下图所示：  
![long类型](https://upload-images.jianshu.io/upload_images/2818100-63474ac944e7e2dd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
在单核场景下，同一时刻只有一个线程执行，禁止CPU中断，这时两个写操作一定是：要么都被执行，要么都没有执行，具有原子性。  
但是在多核场景下，同一时刻可能有两个线程在执行，如果这两个线程同时写long型变量高32位的话，可能会出现开头提及的bug。  

**同一时刻只有一个线程执行，这个条件我们称之为互斥**。如果能够保证对共享变量的修改是互斥的，那么无论是单核CPU还是多核CPU，都能保证原子性。  

### 2.synchronized基本了解
synchronized是锁的一种实现，其可以用来修饰方法、代码块，如下所示：  
```
class X{
  //修饰非静态方法
  synchronized void foo(){
    //临界区
  }
  //修饰静态方法
  synchronized static void bar(){
    //临界区
  }
  //修饰代码块
  Object obj = new Object();
  void baz(){
    synchronized(obj){
      //临界区
    }
  }
}
```
当修饰静态方法时，锁定的是当前类的Class对象，在上例中就是Class X；  
当修饰非静态方法时，锁定的是当前实例对象this。  

&emsp;synchronized关键字解决多线程之间访问资源的同步性，可以保证被它修饰的方法或代码块在任意时刻只能有一个线程执行。  
&emsp;在Java早期版本中，synchronized属于重量级锁，效率低下，因为监视器锁（monitor）是依赖于底层的操作系统的Mutex Lock，Java的线程是映射到操作系统的原生线程上的。  
&emsp;如果要挂起或者唤醒一个线程，需要操作系统帮助完成，而操作系统实现线程之间的切换需要从用户态转换到内核态，这个转换的时间成本是很高的，所以早期synchronized是很低效的。  
&emsp;所以，在JDK6之后从JVM层面对synchronized进行了较大优化，引入了如自旋锁、锁消除、锁粗化、偏向锁、轻量级锁等技术来减少锁操作的开销。 

### 3.如何用一把锁保护多个资源？
**受保护资源和锁之间合理的关联关系应该是N:1的关系**。也就是说，可以用一把锁保护多个资源，不能用多把锁保护一个资源。  
当要保护多个资源时，首先要区分这些资源是否存在关联关系。  

如果要保护相互有关联的多个资源，比如A对B转账这个操作，**需要所有对象都持有一个唯一性的对象，其在创建对象时传入**。  
但是，**更好的办法是，用Account.class作为共享的锁**，Account.class是所有Account对象共享的，且这个对象是JVM在加载Account类的时候创建的，所以我们不用担心其唯一性。代码如下所示：  
```
public class Account {

    private int balance;

    //转账
    void transfer(Account target, int amt){
        //此处检查所有对象共享的锁
        synchronized (Account.class){
            if(this.balance > amt){
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }
}
```
**总结**：对于保护多个资源，关键是要分析多个资源之间的关系。如果资源之间没关系，很好处理，每个资源一把锁即可。如果资源之间有关联关系，就要选择一个粒度更大的锁，这个锁需要覆盖所有相关资源。  
关联关系其实是一种“原子性”特征，**原子性的本质是操作的中间状态对外不可见，要解决原子性问题，要保证中间状态对外不可见**。  

### 4.synchronized的底层原理
#### （1）synchronized同步语句块的情况
```
public class SynchronizedDemo{
  public void method(){
    synchronized(this){
      ....do something....
    }
  }
}
```
&emsp;通过对其编译后的class文件分析发现，synchronized同步语句块的实现使用的是monitorenter和monitorexit指令，其中，monitorenter指令指向同步代码块的
开始位置，monitorexit指令指向同步代码块的结束位置。  
&emsp;当执行monitorenter指令时，线程视图获取锁（monitor的持有权）。monitor对象存在于每个Java对象的对象头中，synchronized就是通过这种方式获取锁的，
这也是为什么Java中任意对象可以作为锁的原因。
&emsp;计数器为0时可以成功获取，相应在执行monitorexit时，计数器设为0，表明锁被释放。如果获取对象锁失败，当前线程就要阻塞等待，直到锁被另一个线程释放。

#### （2）synchronized修饰方法的情况
```
public class SynchronizedDemo2{
  public synchronized void method(){
    ...do something...
  }
}
```
&emsp;这时，并没有monitorenter指令和monitorexit指令，而是ACC_SYNCHRONIZED标识，该标识指明了该方法是一个同步方法，从而执行相应的同步调用。

### 5.如何在项目中使用synchronized？
synchronized最主要的三种使用方式：  
#### （1）修饰实例方法：作用于当前对象实例加锁，进入同步代码前要获得当前对象实例的锁；
#### （2）修饰静态方法：作用于类对象加锁，会作用于当前类的所有对象实例。
#### （3）修饰代码块：指定加锁对象，对给定对象加锁，进入同步代码块前要获得给定对象的锁。

下面介绍一个双重校验锁的单例模式：
```
public class Singleton{
  private volatile static Singleton instance;
  private Singleton(){}
  
  public static Singleton getInstance(){
    //先判断对象是否已经实例过，没有的话才进入加锁代码
    if(instance == null){
      //类对象加锁
      synchronized(Singleton.class){
        if(instance == null){
          instance = new Singleton();
        }
      }
    }
    
    return instance;
  }
}
```
这里注意，为什么要使用volatile？
```
instance = new Singleton();
```
这段代码分为三步执行：  
（1）为instance分配内存空间  
（2）初始化instance  
（3）将instance指向分配的内存地址  

但是由于JVM有指令重排特性，可能执行顺序变为了1->3->2，这是在多线程环境会导致一个线程获得还没有初始化的实例。  
**volatile可以禁止JVM的指令重排。**

### 6.JDK1.6后的synchronized是如何优化的？
&emsp;锁主要存在4种状态：无锁、偏向锁、轻量级锁、重量级锁，他们会随着竞争的激烈逐渐升级。锁可以升级而不可降级，这种策略是为了提高获得锁和释放锁
的效率。

#### （1）偏向锁
&emsp;偏向锁和轻量级锁的引入有相同的原因：在没有多线程竞争的前提下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗。
但不同的是：**轻量级锁在无竞争情况下使用CAS操作去代替使用互斥量，而偏向锁会把整个同步都消除掉。**
&emsp;偏向锁会偏向于第一个获得它的线程，如果在接下来的执行中，该锁没有被其他线程获取，那么持有偏向锁的线程就不需要同步。
&emsp;对于锁竞争激烈的场景，偏向锁会升级为轻量级锁。

#### （2）轻量级锁
&emsp;轻量级锁的加锁和解锁都用到了CAS操作，其能提升程序同步性能的依据是：“对于绝大多数锁，在其整个同步周期内都是不存在竞争的。”  
&emsp;这时，如果锁竞争激烈，轻量级锁会升级为重量级锁。

#### （3）自旋锁和自适应自旋
&emsp;轻量级锁失败后，JVM为了避免线程真实地在操作系统层面挂起，还会进行称为自旋锁的优化手段。
&emsp;**互斥同步对性能最大的影响就是阻塞的实现，因为挂起线程/恢复线程的操作都需要转入内核态中完成。**
&emsp;一般线程持有锁的时间都不是太长，所以仅仅为了这点时间去挂起线程/恢复线程是得不偿失的。所以让一个线程进行一个忙循环等待，这个技术就叫自旋。

&emsp;JDK1.6中引入了自适应的自旋锁，其带来的改进是：自旋的时间不再固定了，而是和前一次同一个锁上的自旋时间以及锁的拥有者状态来决定。

#### （4）锁消除
&emsp;虚拟机在运行时，如果检测到共享数据不可能存在竞争，就执行锁消除。

#### （5）锁粗化
&emsp;通常情况下，要求每个线程持有锁的时间尽可能短，但是一些情况下，对同一把锁的大量获取、释放会消耗大量系统资源。
&emsp;锁粗化就是把多次锁的请求合并成一个锁，降低短时间内大量同步、释放带来的性能损耗。如下代码所示：
```
public class DoSomeThing{
  synchronized(lock){
    ...do something...
  }
  
  //别的操作，不会占用太多时间
  synchronized(lock){
    ...do another thing...
  }
}
```
锁粗化会对其进行合并，如下所示：
```
public class DoSomeThing{
  //进行锁粗化，整合成一次锁请求
  synchronized(lock){
    ...do something...
    //不用占用太多时间的操作
    ...do another thing...
  }
}
```

### 7.synchronized与ReenTrantLock的区别
（1）synchronized是关键字，ReentrantLock是类，这是二者的本质区别。ReentrantLock提供了更多灵活特性：等待可中断、公平锁等。  
（2）synchronized依赖于JVM，而ReentrantLock依赖于API。











