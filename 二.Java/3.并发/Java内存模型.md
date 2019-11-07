导致可见性的原因是缓存，导致有序性的原因是编译优化，那解决可见性、有序性的合理方案应该是**按需禁用缓存和编译优化**。  
Java内存模型规范了JVM如何提供按需禁用缓存和编译优化的方法。具体来说，**这些方法包含volatile、synchronized和final三个关键字**，以及六项**Happens-Before规则**。  

### 1.Happens-Before规则
Happens-Before规则表达的是：**前面一个操作的结果对后续操作是可见的**。其包含以下六项规则：  
#### （1）程序的顺序性规则
这条规则是指在一个线程中，按照程序顺序，前面的操作Happens-Before于后续的任意操作。  

#### （2）Volatile变量规则
这条规则是指对一个volatile变量的写操作， Happens-Before 于后续对这个volatile变量的读操作。  

#### （3）传递性
这条规则是指如果 A Happens-Before B，且 B Happens-Before C，那么 A HappensBefore C。  
如下图所示：  
![传递性](https://upload-images.jianshu.io/upload_images/2818100-45a7690e76257c6e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
如果B读到了v=true，那么A设置的x=42对B是可见的。  

#### （4）管程中锁的规则
这条规则是指对一个锁的解锁 Happens-Before 于后续对这个锁的加锁。  
管程是一种通用的同步原语，在 Java 中指的就是 synchronized，synchronized 是 Java 里对管程的实现。  

#### （5）线程start()规则
这条是关于线程启动的。它是指主线程 A 启动子线程 B 后，子线程 B 能够看到主线程在启动子线程 B 前的操作。  

#### （6）线程join()规则
这条关于线程等待。指的是主线程A等待子线程B完成（A通过调用B的join()方法实现），当B完成后（A中join()返回），A能看到子线程的操作。这里指的是**对共享变量**的操作。  
例如下面代码：  
```
Thread B = new Thread(()->{
  //此处对共享变量var做修改
  var = 66;
});
//例如此处对共享变量修改，则这个修改结果对线程B可见
B.start();
B.join();
//子线程中所有对共享变量的修改，在主线程调用B.join()之后皆可见，此例中var==66。
```

### 2.synchronized与volatile的区别
（1）volatile本质是告诉JVM当前变量在寄存器（工作内存）中的值是不确定的，需要从主存中读取；synchronized则是锁定当前变量，只有当前线程可以访问该
变量，其他线程被阻塞。  
（2）volatile只能修饰变量；synchronized可以修饰方法、变量以及类。  
（3）volatile只能实现变量的修改可见性，不能保证原子性；synchronized可以保证变量的修改可见性和原子性。  
（4）volatile不会造成线程阻塞。  
（5）volatile标记变量不会被编译器优化。  

### 3.对volatile的理解
**volatile关键字用来保证有序性和可见性。**
首先，编译器和CPU会对我们写的代码做重排序，这样是为了减少流水线阻塞，提高CPU执行效率。在重排序的过程中要遵循happens-before原则，其中对于volatile
有这样一条规则：一个变量的写操作先行发生于之后对这个变量的读操作。有序性的实现是通过插入内存屏障来实现的。

被volatile修饰的变量，有以下两点特性：  
（1）保证不同线程对该变量操作的内存可见性。  
（2）阻止指令重排序。

