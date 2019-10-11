1.==和equals区别  
  “==”如果比较的是基本数据类型，比较值；如果比较引用类型，比较地址。
  “equals”比较两个对象内容是否相等。如果没有重写，比较的是引用类型的变量指向的对象地址（所以一般情况下都要重写）。
  
2.两个对象hashCode()相同，equals一定相同吗？  
  不一定。除非两者计算方法相同，所以一般两个方法都要重写。
  
3.为什么重写equals()一定要重写hashCode()方法？  
https://mp.weixin.qq.com/s/PGWEBIyQsUaImmKFNa__Bw  
