### 1.什么是ThreadLocal？
ThreadLocal提供线程局部变量。**ThreadLocal适用于每个线程需要自己独立的实例且该实例需要在多个方法中被使用（相同线程数据共享），且这些变量
在线程间隔离（不同线程数据隔离）。**

#### 如何使用？
```
public class MyThreadLocalDemo{
  private static ThreadLocal<String> threadLocal = new ThreadLocal<>();
  
  private String getString(){
    return threadLocal.get();
  }
  
  private void setString(String string){
    threadLocal.set(string);
  }
  
  public static void main(Stirng[] args){
    int threads = 9;
    MyThreadLocalDemo demo = new MyThreadLocalDemo();
    CountDownLatch cdl = new CountDownLatch(threads);
    for(int i = 0; i < threads; i++){
      Thread thread = new Thread(
        ()->{
          demo.setString(Thread.currentThread().getName());
          System.out.printLn(demo.getString());
          countDownLatch.countDown();
        },"thread - " + i);
        thread.start();
    }
  }
}
```

这样，就解决了线程间数据隔离的问题。

### 2.ThreadLocal原理分析
**对象实例与ThreadLocal变量的映射关系是由线程Thread来维护的。**
对象实例与ThreadLocal变量的映射关系是存放在一个Map里面，这个Map是Thread类中的一个字段，称为ThreadLocalMap。  
详情请见：https://mp.weixin.qq.com/s/vURwBPgVuv4yGT1PeEHxZQ





