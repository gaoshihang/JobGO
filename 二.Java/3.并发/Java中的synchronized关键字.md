### 1.synchronized基本了解
&emsp;synchronized关键字解决多线程之间访问资源的同步性，可以保证被它修饰的方法或代码块在任意时刻只能有一个线程执行。  
&emsp;在Java早期版本中，synchronized属于重量级锁，效率低下，因为监视器锁（monitor）是依赖于底层的操作系统的Mutex Lock，Java的线程是映射到操作系统
的原生线程上的。  
&emsp;如果要挂起或者唤醒一个线程，需要操作系统帮助完成，而操作系统实现线程之间的切换需要从用户态转换到内核态，这个转换的时间成本是很高的，所以早期
synchronized是很低效的。  
&emsp;所以，在JDK6之后从JVM层面对synchronized进行了较大优化，引入了如自旋锁、锁消除、锁粗化、偏向锁、轻量级锁等技术来减少锁操作的开销。

### 2.synchronized的底层原理
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

### 3.如何在项目中使用synchronized？
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

### 4.JDK1.6后的synchronized是如何优化的？
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









