### 1.什么是类加载机制？
虚拟机把描述类的数据从Class文件加载到内存，并对数据进行校验、转换解析和初始化，最终形成可以被虚拟机直接使用的Java类型，这就是类加载机制。  
类从被加载到内存中，到卸载出内存为止，其整个生命周期包括七个阶段：**加载、验证、准备、解析、初始化、使用、卸载**。其中验证、准备、解析3个部分称为连接，
如图所示：  
![类加载的7个阶段](https://img-blog.csdn.net/20180105165447562?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvZmVpZ2Vzd2p0dQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
