如果线程要求的条件不满足，则线程阻塞自己，进入**等待**状态；当线程要求的条件满足后，**通知**等待的线程重新执行。  

**一个完整的等待-通知机制**：线程首先获取互斥锁，当线程要求的条件不满足时，释放互斥锁，进入等待状态；当线程要求的条件满足时，通知等待的线程，重新获取互斥锁。  

### 1.用synchronized实现等待-通知机制
synchronized配合wait、notify和notifyAll这三个方法能轻松实现等待-通知机制。  
当一个线程进入到synchronized保护的临界区时，如果某些条件不满足，需要进入等待状态，Java对象的wait()方法即可满足。当调用wait()方法后，当前线程会被阻塞，进入
一个等待队列，同时，**释放持有的互斥锁**，这时，其他线程就有机会获得锁，进入临界区。  
当线程要求的条件满足时，可以通过Java对象的notify和notifyAll方法，通知这个等待的线程。当条件满足时调用notify，会通知等待队列中的线程，**告诉它条件曾经满足过**。  
**notify只能保证在通知时间点，条件是满足的。而被通知线程的执行时间点和通知时间点基本上不会重合，所以当线程执行的时候，很可能条件已经不满足了**。  
wait、notify、notifyAll这三个方法能够被调用的前提是已经获取了相应的互斥锁，**所以这三个方法都是在synchronized内部被调用的**。  

### 2.例子
下面来看一个例子，如何解决一次性申请转出账户和转入账户的问题。  
```
public class Allocator {
    private List<Object> als;

    //一次性申请所有资源
    synchronized void apply(Object from, Object to){
        //经典写法
        while(als.contains(from) || als.contains(to)){
            try {
                wait();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        als.add(from);
        als.add(to);
    }

    synchronized void free(Object from, Object to){
        als.remove(from);
        als.remove(to);
        notifyAll();
    }
}
```

### 3.尽量使用notifyAll
notify会随机通知等待队列中的一个线程，而notifyAll会通知等待队列中的所有线程。使用notify可能某些线程永远不会被通知到。  

### 4.面试题：wait方法和sleep方法的区别
（1）wait会释放所有锁，而sleep不会释放锁；  
（2）wait只能在同步方法喝同步块中使用，而sleep任何地方都可以；  
（3）wait无需捕捉异常，而sleep需要；  
（4）sleep是Thread的方法，而wait是Object的；  
（5）sleep方法调用时必须指定时间。  

### 5.使用wait的正确姿势
wait需要在while循环中被调用。   
```
while(条件不满足){
    wait();
}
```

### 6.何时使用notify？
需要满足以下三个条件：  
（1）所有等待线程拥有相同的等待条件；  
（2）所有等待线程被唤醒后，执行相同的操作；  
（3）只需要唤醒一个线程。  








