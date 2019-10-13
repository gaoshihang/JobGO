常用容器类包括Collection和Map两种，Collection存储着对象集合，而Map存储着键值对（两个对象）的映射表。

### Collection
Collection包含Set、List和Queue。
#### 1.Set
**TreeSet**
基于红黑树实现，支持有序性操作，例如：根据一个范围查找元素。查找效率不如HashSet（O(1)），TreeSet为O(logn)。

**HashSet**
基于哈希表实现，支持快速查找，但不支持有序性操作，且失去了元素插入的顺序信息。

**LinkedHashSet**
具有HashSet的查找效率，内部使用双向链表维护元素的插入顺序。

#### 2.List
**ArrayList**
基于动态数组实现，支持随机访问。

**Vector**
ArrayList的线程安全版本，方法为锁，效率较低。

**LinkedList**
基于双向链表实现，只能顺序访问，但是可以快速地在链表中间插入和删除元素。不仅如此，还可以用作栈、队列和双向队列。

#### 3.Queue
**LinkedList**
可以用来实现双向队列。

**PriorityQueue**
基于堆结构实现，可以用来实现优先队列。


### Map
**TreeMap**
基于红黑树实现。

**HashMap**
基于哈希表实现。

**HashTable**
是HashMap的线程安全版本，同一时刻多个线程可以同时写入HashTable且不会导致数据不一致。
不应该使用这个类，而应该用ConcurrentHashMap，效率更高（分段锁）。

**LinkedHashMap**
使用双向链表来维护元素顺序，顺序为插入顺序或最近最少使用（LRU）顺序。




