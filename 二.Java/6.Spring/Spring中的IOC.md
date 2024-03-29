### 1.什么是IOC？
IOC：Inversion of Control，翻译为控制反转。简单来说就是把复杂系统分解成互相合作的对象。  
IOC理论的观点是：借助于“第三方”实现具有依赖关系的对象之间的解耦。  
![Spring IOC](https://pic002.cnblogs.com/images/2011/230454/2011052709391014.jpg)  
由于引进了中间位置的“第三方”，也就是IOC容器，使得A,B,C,D这4个对象没有了耦合关系，全部对象的控制权全部上缴给“第三方”IOC容器。  

下面来看看，控制反转（IOC）的名字是如何得来的？  
在没有引入IOC容器之前，对象A依赖于对象B，那么对象A在初始化或运行到某一点时，自己必须主动创建对象B或者使用已经创建的对象B。无论是创建还是使用对象B，，
控制权都在自己手上。  
在引入IOC容器后，对象A与对象B之间失去了直接联系，当对象A运行到需要对象B的时候，IOC容器主动创建一个对象B注入到对象A需要的地方。  
通过前后对比：对象A获得依赖对象B的过程，由主动行为变为了被动行为，控制权颠倒了，这就是“控制反转”这个名字的意思。  

