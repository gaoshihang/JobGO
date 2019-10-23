#### （1）事务
MyISAM不支持事务，InnoDB支持；  

#### （2）全文索引
MyISAM支持全文索引，InnoDB不支持；  

#### （3）关于count(*)
MyISAM存储着数据总行数，count(*)时会直接返回，而InnoDB会一行行扫描；  

#### （4）外键
MyISAM不支持外键，InnoDB支持外键；  

#### （5）锁
MyISAM只支持表锁，而InnoDB可以支持行锁；  

