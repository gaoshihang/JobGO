**Hive能够直接处理输入的SQL语句，调用MapReduce计算框架完成数据分析操作**。其架构图如下：  
![Hive架构图](https://static001.geekbang.org/resource/image/26/ea/26287cac9a9cfa3874a680fdbcd795ea.jpg)  

可以通过Hive的Client或JDBC等向Hive提交SQL命令。**如果是创建数据库的DDL（数据定义语言）**，Hive会通过之执行引擎Driver将数据表的信息记录在Metastore元数据
组件中，这个组件通常使用一个关系型数据库，记录表名、字段名、字段类型、关联HDFS文件路径等这些数据库的Meta信息（元信息）。  
**如果提交的是查询分析数据的DQL（数据查询语句），Driver会将该语句提交给编译器Compiler进行语法分析、语法解析、语法优化等一系列操作**，最后生成一个MapReduce执行计划，提交给计算框架处理。  
Hive的主要处理过程大体分为以下三步：  
（1）将输入的Hive QL经过语法解析器转换成Hive抽象语法树（Hive AST）；  
（2）将Hive AST经过语义分析器转换成MapReduce执行计划；  
（3）将生成的MapReduce执行计划和Hive执行函数代码提交到Hadoop上执行。  

例如下面sql语句：  
```
select * from status_updates where status like 'michael jackson';
```
其对应的Hive执行计划如下：  
![执行计划](https://static001.geekbang.org/resource/image/cb/a6/cb1236ad035ca01cffbb9df47fa88fa6.jpg)  

### Hive如何进行join操作？
如下sql语句需要连接两张表：  
```
select pv.pageid, u.age from page_view pv join user u on (pv.userid = u.userid);
```

两张表的示例图如下：  
![hive连接](https://static001.geekbang.org/resource/image/82/2d/8254710229b1d749d08f7a0bb799ac2d.jpg)  

join的mapreduce计算过程与之前的不同，因为join来自两个表（文件夹），需要在map阶段标识，比如来自第一张表的输出Value就记录为<1, X>，这里的1表示数据来自第一张表。
这样经过shuffle以后，相同的Key被输入到同一个reduce函数，就可以根据表的标记对Value数据求笛卡尔积，用第一张表的每条记录和第二张表的每条记录连接，输出就是join的结果。  
如下图所示：  
![hive连接过程](https://static001.geekbang.org/resource/image/25/2a/25d62b355c976beb5b26af865ac2b92a.jpg)  



