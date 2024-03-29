例题：InnoDB一棵B+树可以存放多少行数据？约2千万。  

计算机在存储数据的时候，有最小存储单元。磁盘存储数据的最小单元是扇区，一个扇区的大小是512字节，而文件系统（例如XFS/EXT4）的最小单元是块，一个块的大小是
4k。  
而对于InnoDB存储引擎，也有自己的最小存储单元——页（Page），一个页的大小是16K。所以InnoDB的所有数据文件（后缀为ibd的文件），其大小始终都是16384（16k）的整数倍。  

数据都是存储在页中，所以一个页中能存储多少行数据呢？假设一行数据的大小是1K，那么一页可以存储16行数据。  
这时，如何查找成了一个问题，我们不可能把所有的页都遍历一遍，那样太慢了。  

所以Mysql用了B+树的方式来组织这些数据，如图所示：  
![B+树](https://upload-images.jianshu.io/upload_images/7862980-65dd732f056b4af2.png?imageMogr2/auto-orient/strip|imageView2/2/w/761/format/webp)  

我们将数据按照主键进行排序，分别存放在不同的页中。除了存放数据的页以后，还有存放键值+指针的页，如图中灰色的页，这样的页由N个键值+指针组成。它也是排序的，
这样的数据组织方式，我们称之为索引组织表。  

在查找数据时，比如以下语句：  
```
select * from user where id=5;
```
id为主键，我们先找到根页（每张表的根也位置在表空间文件中是固定的，就是page number=3的页）。找到后通过二分查找，定位到其指针指向的页中，进一步去这个页中查找。  

下面进行一些总结：  
* InnoDB存放数据的最小单元是页，**页可以用于存放数据也可以用于存放键值+指针，在B+树中叶子结点存放数据，非叶子结点存放键值+指针**。  
* 索引组织表通过非叶子结点的二分查找法以及指针确定数据在哪个页中，进而去数据页中查找需要的数据。  

回到最初的问题，通常一棵B+树能够存放多少行数据？  
假设B+树高为2，即存在一个根结点和若干个叶子结点，那么这棵B+树的存放总记录数为：根结点指针数*单个叶子结点记录行数。  
上文已经说明单个叶子结点页中的记录数为16K/1k=16行。  
现在需要计算出非叶子结点能存放多少指针？假设主键ID为bigint类型，长度为8字节，而指针大小在InnoDB源码中设置为6字节，这样一共14字节。  
那么一个页中能存放的单元数为：16384/14=1170。  
可以算出一棵高为2的B+树，可以存放1170*16=18720条数据。  

同理，高为3的B+树可以存放1170*1170*16=21902400条记录  
**所以在InnoDB中B+树高度一般为1-3层，它就可以满足千万级的数据存储。**  
查找数据时一次页的查找代表一次IO，则通常只需要1-3次IO就可以查找到数据。  

**重要题目：为什么MYSQL用B+而不是B树？**  
因为B树不管叶子节点还是非叶子结点，都会保存数据，这样导致在非叶子结点中能保存的指针数量变少。这种情况下要存储大量数据，只能增加树的高度，这无疑增加了IO操作次数。  



