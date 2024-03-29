### 1.IO流的分类
（1）按功能分：输入流（input）、输出流（output）  
（2）按类型分：字节流和字符流。  
（3）字节流：InputStream和OutputStream是字节流的抽象类，这两个抽象类又派生了若干子类，不同子类分别处理不同操作类型。  
（4）字符流：Reader和Writer是字符的抽象类，这两个抽象类也派生了若干子类。  

### 2.字节流和字符流有什么区别？
字节流按照8位传输，以字节为单位输入输出数据；  
字符流按16位传输，以字符为单位输入输出数据。Reader/Writer则是用于操作字符，增加了字符编解码等功能，适用于类似从文件中读取或者写入文本信息。本质上计算机操作的都是字节，不管是网络通信还是文件读
取，Reader/Writer相当于构建了应用逻辑和原始数据之间的桥梁。

### 3.BIO、NIO、AIO有什么区别？
（1）BIO：Block IO，称为同步阻塞式IO，特点是使用方便，并发处理能力低。BIO模式下，数据的读写必须阻塞在一个线程内等待完成。在活动连接数不高的情况下，效率不错。  
（2）NIO：New IO，称为同步非阻塞式IO，是BIO的升级。客户端和服务器端通过Channel通讯，实现了多路复用。在Java.nio包中，提供了Channel、Selector、Buffer等抽象，其中Selector是使用单线程轮询的方式监控多个Channel，实现多路复用。  
（3）AIO：Asynchronous IO是异步非阻塞IO，其基于事件和回调机制，应用操作后直接返回，不会阻塞，当后台处理完成后，操作系统会通知相应的线程进行后续处理。

