#### 1.阻塞IO
进程调用读取指令后阻塞直至数据复制到内存完毕，一个进程或线程对应一个连接。  
![阻塞IO](https://upload-images.jianshu.io/upload_images/15425898-1a42d6ffa9b0566d.png?imageMogr2/auto-orient/strip|imageView2/2/w/539/format/webp)  

#### 2.非阻塞IO
进程调用读取指令后可以处理其他任务后再查看数据是否准备完毕，准备完毕后阻塞至数据从IO设备复制到内存中。  
![非阻塞IO](https://upload-images.jianshu.io/upload_images/15425898-844cbf580c50c9e8.png?imageMogr2/auto-orient/strip|imageView2/2/w/574/format/webp)  

#### 3.多路复用IO
改进的**阻塞IO**，可以处理多个连接，涉及select、poll、epoll IO多路复用是阻塞在select epoll这样的系统调用之上，而没有阻塞在真正的I/O系统调用如recvfrom上。  
![多路复用IO](https://upload-images.jianshu.io/upload_images/15425898-be860b3c760335ce.png?imageMogr2/auto-orient/strip|imageView2/2/w/483/format/webp)  

#### 4.信号驱动IO
进程调用读取指令后内核在IO设备数据准备完毕后通过回调函数使进程阻塞至数据从IO设备复制到内存完毕。  
![信号驱动IO](https://upload-images.jianshu.io/upload_images/15425898-7e681353904f8277.png?imageMogr2/auto-orient/strip|imageView2/2/w/481/format/webp)  

#### 5.AIO
进程调用读取指令后内核负责处理数据从IO设备直至复制至内存后再回调进程函数。  
![AIO](https://upload-images.jianshu.io/upload_images/15425898-dfb72a825b08d8a5.png?imageMogr2/auto-orient/strip|imageView2/2/w/478/format/webp)  

