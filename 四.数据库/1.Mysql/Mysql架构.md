### 1.Mysql架构
以下是Mysql的架构图：  
![Mysql基础架构](https://img-blog.csdnimg.cn/2019021622430652.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2J3dDE5ODk=,size_16,color_FFFFFF,t_70)  
大体来说，Mysql可以分为Server层和存储引擎两部分。  
Server层包括：连接器、查询缓存、分析器、优化器、执行器等，覆盖了Mysql大多数核心服务功能，以及所有内置函数（如：日期、时间、数字和加密函数等），所有跨存储
引擎的功能都在这一层实现，比如：视图、存储过程等等。  
存储引擎层负责：数据的存储和提取，其架构是插件式的。  

### 2.一条SQL在数据库中的执行流程
（1）应用程序把查询SQL语句发送给服务器端执行；  
（2）如果查询缓存打开，则先去查询缓存中查找，如果存在，直接返回给客户端，否则进行下面操作；  
（3）解析SQL、预处理、优化SQL执行计划等；  
（4）根据执行计划完成整个查询；  
（5）将查询结果返回给客户端。  
具体流程如下：https://blog.csdn.net/pcwl1206/article/details/86137408
