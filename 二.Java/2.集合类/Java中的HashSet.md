### 1.HashSet的实现原理
HashSet的实现是依赖于HashMap的，其值都是存储在HashMap中的。
HashSet不允许重复，这是因为HashSet的值是作为HashMap的key存储在HashMap中的。

### 2.HashSet怎么保证元素不重复的？
```
public boolean add(E e){
  return map.put(e, PRESENT) == null;
}
```
元素值作为map的key，value是PRESENT变量，这个变量只作为放入map的一个占位符存在，没有实际用途。
