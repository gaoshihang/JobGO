### 1.如何进行Mysql问题排查？
（1）使用show processlist命令查看当前所有连接信息；  
（2）使用Explain命令查询SQL语句执行计划；  
（3）开启慢查询日志，查看慢查询SQL。  

### 2.Mysql CPU 飙升到500%怎么处理？
（1）使用show processlist列出所有进程，将多秒没有状态的干掉；  
（2）查看超时日志或错误日志（一般会使查询以及大批量的插入导致CPU与I/O上涨）。  

