### 1.HashMap实现原理/底层数据结构？JDK1.7和1.8
**JDK1.7**：Entry数组+链表
**JDK1.8**：Node数组+链表/红黑树，当链表上元素个数超过8且数组长度>=64时，自动转化成红黑树，节点变为树节点，提高搜索效率和插入效率到O(logN)。

### 2.HashMap的put方法执行过程？
往HashMap添加一对key-value时，首先计算key的hash值，然后根据hash值确认在table中存储的位置。若该位置没有元素，则直接插入。
否则迭代该处元素链表并依次比较其key的hash值，若hash相等且key相等，则用新的Entry的value覆盖原来节点的value。
若hash相等但key值不等，则将该节点插入到链表的表头。

### 3.HashMap的get方法执行过程？
通过key的hash值找到数组中索引处的Entry，然后返回该key对应的value。

### 4.HashMap的resize方法执行过程？
两种情况下会调用resize方法：  
（1）第一次调用HashMap的put时，会调用resize对table数组进行初始化，如果不传入值，则默认大小为16.  
（2）扩容时调用resize，即当size > threshold时，table数组大小翻倍，threshold = 数据长度 * loadFactor。  

每次扩容完成后容量都翻倍。**扩容后要将原数组中的所有元素找到在新数组中合适的位置。**

### 5.为什么HashMap的size必须是2的整数次方？
在HashMap中，元素映射到数组的具体位置要经过一个映射算法，即(n-1) & hash。

下面演示一下这个算法：  
（1）假设有一个key=“book”  
（2）计算book的hashCode值，结果为十进制的3029737，二进制的1011100011101011101001。  
（3）假定HashMap长度是默认的16，计算Length-1的结果为十进制的15，二进制的1111.  
（4）把以上两结果做与运算，1011100011101011101001 & 1111 = 1001，十进制是9，所以index=9.  

通过这种与运算的方式，能够和取模运算一样的效果hashCode % length，上述例子就是 3029737 % 16 = 9.

**通过位运算大大提高了性能**。

**长度16或者其他2的幂，Length-1的值是所有二进制位全为1，这种情况下，index的结果等同于HashCode后几位的值。只要输入的HashCode本身分布均匀，Hash算法的结果就是均匀的。**

### 6.HashMap的多线程死循环问题
多线程同时put时，如果同时触发了rehash操作，会导致HashMap中的链表中出现循环节点，使得后面get的时候，出现死循环。  
详细请见：https://coolshell.cn/articles/9606.html

### 7.HashMap的get方法能否判断某个元素是否在map中？
**不能**。如果get返回null，可能是HashMap中不包含该key，也有可能该key的value为null。因为HashMap中允许key为null，也允许value为null。

### 8.HashMap与HashTable的区别
（1）HashMap的key和value都允许为null，而Hashtable不允许。  
（2）Hashtable是线程安全的，而HashMap不是。可以使用Collections.synchronizedMap(hashMap)使其实现同步。  
（3）HashMap的迭代器（Iterator）是fail-fast迭代器，而HashTable的enumerator迭代器不是fail-fast的。所以当有其他线程改变了HashMap的结构（增加或者删除元素），将会抛出ConcurrentModificationException，但迭代器本身的remove()方法移除元素不会抛出这个异常。




