### 1.ArrayList和LinkedList的区别
**ArrayList**：底层基于数组实现，查找快，增删较慢。
**LinkedList**：底层基于双向链表实现，查找慢、增删快。

**注意**：  
（1）ArrayList的增删不一定比LinkedList慢，如果增删都是在其末尾进行（每次调用都是remove()和add()），此时ArrayList不需要移动和复制数组。  
（2）如果删除操作的位置在中间，由于LinkedList的消耗主要在遍历上，ArrayList消耗主要在移动和复制上（底层调用arrayCopy()方法，是native方法）。如果数据量有百万级别，LinkedList的遍历速度要慢于ArrayList的复制移动速度的。


### 2.为什么ArrayList实现了RandomAccess接口而LinkedList没有？
源码如下：
```
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
        
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```

Random接口是空的：
```
public interface RandomAccess {
}
```

#### RandomAccess接口
RandomAccess接口是一个标记接口，只要List实现这个接口，就能支持快速随机访问。什么是随机访问呢？
来看一下Collections工具类中的二分搜索代码：
```
public static <T>
    int binarySearch(List<? extends Comparable<? super T>> list, T key) {
        if (list instanceof RandomAccess || list.size()<BINARYSEARCH_THRESHOLD)
            return Collections.indexedBinarySearch(list, key);
        else
            return Collections.iteratorBinarySearch(list, key);
    }
```
如代码所示：实现了RandomAccess的实例执行indexedBinarySearch方法，否则执行iteratorBinarySearch方法，如下所示：
```
private static <T>
int indexedBinarySearch(List<? extends Comparable<? super T>> list, T key) {
    int low = 0;
    int high = list.size()-1;

    while (low <= high) {
        int mid = (low + high) >>> 1;
        Comparable<? super T> midVal = list.get(mid);
        int cmp = midVal.compareTo(key);

        if (cmp < 0)
            low = mid + 1;
        else if (cmp > 0)
            high = mid - 1;
        else
            return mid; // key found
    }
    return -(low + 1);  // key not found
}

private static <T>
int iteratorBinarySearch(List<? extends Comparable<? super T>> list, T key)
{
    int low = 0;
    int high = list.size()-1;
    ListIterator<? extends Comparable<? super T>> i = list.listIterator();

    while (low <= high) {
        int mid = (low + high) >>> 1;
        Comparable<? super T> midVal = get(i, mid);
        int cmp = midVal.compareTo(key);

        if (cmp < 0)
            low = mid + 1;
        else if (cmp > 0)
            high = mid - 1;
        else
            return mid; // key found
    }
    return -(low + 1);  // key not found
}
```
可以看到，实现了RandomAccess接口的List使用索引遍历，否则使用迭代器遍历。**这是为什么？**  
**原因**：ArrayList通过for遍历比通过iterator遍历要稍快，LinkedList通过iterator遍历比通过for遍历要快。

### 3.ArrayList的扩容机制
阿里的Java手册上有这样一句话：“在集合初始化时，指定其初始值大小”。**这是为什么呢？**  
通过测试发现，设置了初始值大小的ArrayList在性能上会比没有设置的ArrayList产生足足数量级的差距。

接下来我们看看ArrayList的源码
#### 初始化
有以下三个构造方法：  
（1）用指定大小初始化内部数组
```
public ArrayList(int initialCapacity)
```

（2）默认构造器，默认大小初始化内部数组
```
public ArrayList()
```

（3）接收Collection对象，并将其添加到ArrayList中
```
public ArrayList(Collection<? extends E> c)
```

#### 动态扩容
无参构造器的内部数组初始长度为0，add方法的源码如下：
```
public boolean add(E e){
  ensureCapacityInternal(size + 1);
  elementData[size++] = e;
  return true;
}
```

其中，ensureCapacityInternal是为了保证内部容量，不足时，进行扩容操作；
下面来看看扩容的具体流程：
##### （1）确保内部容量
先判断是否需要扩容：  
```
private static final int DEFAULT_CAPACITY = 10;
private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

private void ensureCapacityInternal(int minCapacity){
  //如果elementData数组是一个空数组的话，最小需要容量就是默认容量
  if(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA){
    minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
  }
  
  ensureExplicitCapacity(minCapacity);
}

private void ensureExplicitCapacity(int minCapacity){
  modCount++;
  //如果elementData数组的长度小于最小需要的容量（minCapacity），则进行扩容
  if(minCapacity - elementData.length > 0){
    //扩容方法
    grow(minCapacity);
  }
}
```
简单说就是当传入的最小需要容量（也就是数组中的实际元素个数+1）大于等于数组容量的时候，就进行扩容。

##### （2）扩容
下面来看它是如何扩容的？
```
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

private void grow(int minCapacity){
  int oldCapacity = elementData.length;
  //位运算向右移动一位，意味着做除法，但是位运算效率更高
  //相当于newCapacity = oldCapacity + 0.5 * oldCapacity
  //就是说ArrayList扩容是每次1.5倍
  int newCapacity = oldCapacity + (oldCapacity >> 1);
  if(newCapacity - minCapacity < 0){
    //取较大的一个值
    newCapacity = minCapacity;
  }
  
  if(newCapacity - MAX_ARRAY_SIZE > 0){
    newCapacity = hugeCapacity(minCapacity);
  }
  
  //元素复制
  elementData = Arrays.copyOf(elementData, newCapacity);
}
```

##### （3）添加元素
上述容量判断结束后，真正开始添加元素到elementData数组中。  
**另外一点需要注意**：ArrayList在使用默认构造方法初始化时，会延迟分配数组对象空间，只在第一次真正插入元素的时候才会分配。默认为10.

**为什么会建议集合初始化时尽量指定大小？**  
因为这样可以减少扩容次数，进而提高代码执行效率。






