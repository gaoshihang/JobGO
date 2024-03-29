### 1.悲观锁与乐观锁的理解
#### 悲观锁
总是假设最坏的情况，每次拿数据的时候都认为别人会修改，所以都会上锁，这样别人就会阻塞。
传统关系型数据库的行锁、表锁、读锁、写锁等都是这种机制。  
Java中的synchronized和ReentrantLock也是这种思想。

#### 乐观锁
总是假设最好情况，认为别人不会修改，所以不会上锁，但是在更新的时候会判断这期间别人有没有修改这个数据。可以使用版本机制和CAS算法实现。
乐观锁适合于读多写少的场景，Java中的atomic包下的原子变量类就使用了乐观锁的CAS实现的。

#### 两种锁的使用场景
以上的两种锁各有优缺点，如果是读多写少的场景，使用乐观锁比较合适，可以降低锁的开销；如果是写多的场景，还是使用悲观锁。

### 2.乐观锁常见的两种实现方式
#### （1）版本号机制
一般在数据表中加一个数据版本号version字段，表示数据被修改的次数，当数据被修改时，version值会加1。当A要更新值的时候，同时会读取version值，在提交更新时，若刚才读到的version值与数据库中现在的version值相等时才更新，否则重试更新操作。

#### （2）CAS算法
Compare and Swap（比较与交换），是一种有名的无锁算法。
无锁编程：不使用锁的情况下实现多线程之间的变量同步，也就是在没有线程被阻塞的情况下实现变量的同步。

CAS算法涉及三个操作数：  
（1）需要读写的内存值V  
（2）进行比较的值A  
（3）写入的值B  

当V=A时，CAS通过原子操作方式用新值B更新V。否则不执行任何操作。

### 3.乐观锁的缺点
#### （1）ABA问题
初次读取的时候是A值，准备赋值的时候是A，但是可能在这段时间里A被改为别的值，又改回A。

#### （2）循环时间长开销大
自旋CAS（不成功时一直循环直到成功），如果长时间不成功，会给CPU带来非常大的执行开销。

#### （3）只能保证一个共享变量的原子操作
CAS只能对单个共享变量起作用，当操作涉及到多个共享变量时CAS无效。
从JDK1.5开始，提供了AtomicReference类，保证引用对象之间的原子性，可以把多个变量放在一个对象里进行CAS操作。
