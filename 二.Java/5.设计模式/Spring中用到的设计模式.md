#### （1）单例模式
Spring中的Bean默认都是单例的。  

#### （2）代理模式
Spring AOP功能实现。  

#### （3）工厂设计模式
Spring使用工厂模式通过BeanFactory、ApplicationContext创建Bean对象。  

#### （4）模板方法模式
jdbcTemplate、hibernateTemplate等以Template结尾的对数据库操作的类，它们用到了模板模式。  

#### （5）装饰器模式
项目需要连接多个数据库，且不同的客户在每次访问中根据需要会去访问不同的数据库。这种模式让我们可以根据客户的需求能够动态切换不同的数据源。  

#### （6）观察者模式
Spring事件驱动模型使用了观察者模式。  




