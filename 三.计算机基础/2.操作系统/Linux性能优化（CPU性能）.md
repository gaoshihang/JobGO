## 一.怎么理解平均负载？
以下是uptime命令的输出：  
```
$ uptime
02:34:03 up 2 days, 20:14,  1 user,  load average: 0.63, 0.83, 0.88
```
而最后三个数字呢，依次则是过去1分钟、5分钟、15分钟的平均负载（Load Average）。  

**什么是平均负载**？
平均负载指的是单位时间内，系统处于可运行状态和不可中断状态的平均进程数，也就是**平均活跃进程数**，其和CPU使用率没有直接关系。  
**可运行状态**：是指正在使用CPU或正在等待CPU的进程，也就是常用ps命令看到的，处于R状态（Running或Runnable）的进程。  
**不可中断状态**：正处于内核态关键流程中的进程，且这些流程是不可打断的，比如常见的是等待硬件设备的I/O响应，也就是在ps命令中看到的D状态（Uninterruptible Sleep，也称为Disk Sleep）的进程。  
**不可中断状态实际上是系统对进程和硬件设备的一种保护机制**。  

既然平均的是活跃进程数，那么最理想的，就是每个CPU上都刚好运行着一个进程，这样每个CPU都得到了充分利用。比如当平均负载为2时，意味着什么呢？  
* 在只有2个CPU的系统上，意味着所有的CPU都刚好被完全占用；  
* 在4个CPU的系统上，意味着CPU有50%的空闲；  
* 在只有1个CPU的系统中，意味着有一半的进程竞争不到CPU。  

### 1.平均负载为多少时合理？  
平均负载最理想的情况是等于CPU个数。CPU个数可以使用 top 命令或者从文件 /proc/cpuinfo 中读取，比如：  
```
grep 'model name' /proc/cpuinfo | wc -l
```

当平均负载高于 CPU 数量70%的时候，你就应该分析排查负载高的问题了。一旦负载过高，就可能导致进程响应变慢，进而影响服务的正常功能。  

### 2.平均负载与CPU使用率
平均负载是指单位时间内，处于可运行状态和不可中断状态的进程数。所以，它不仅包括了正在使用 CPU 的进程，还包括等待 CPU 和等待 I/O 的进程。  
而 CPU 使用率，是单位时间内 CPU 繁忙情况的统计，跟平均负载并不一定完全对应。比如：  
* CPU 密集型进程，使用大量 CPU 会导致平均负载升高，此时这两者是一致的；  
* I/O 密集型进程，等待 I/O 也会导致平均负载升高，但 CPU 使用率不一定很高；  
* 大量等待 CPU 的进程调度也会导致平均负载升高，此时的CPU使用率也会比较高。  

## 二.CPU上下文切换
Linux是一个多任务操作系统，支持远大于CPU数量的任务同时运行。但是，这些任务实际上不是真的在同时运行，而是在很短的时间内轮换使用CPU。  
在运行每个任务前，CPU需要知道任务从哪里加载、从哪里运行，也就是说，**需要系统事先帮它设置好CPU寄存器和程序计数器**。  
CPU 寄存器，是 CPU 内置的容量小、但速度极快的内存。而程序计数器，则是用来存储 CPU 正在执行的指令位置、或者即将执行的下一条指令位置。它们都是 CPU 在运行任何任务前，必须的依赖环境，因此也被叫做 **CPU 上下文**。  
CPU 上下文切换，就是先把前一个任务的 CPU 上下文（也就是 CPU 寄存器和程序计数器）保存起来，然后加载新任务的上下文到这些寄存器和程序计数器，最后再跳转到程序计数器所指的新位置，运行新任务。  
**根据任务的不同，CPU 的上下文切换就可以分为几个不同的场景，也就是进程上下文切换、线程上下文切换以及中断上下文切换。**  
下面来看看它们为什么能造成性能问题？  

### 1.进程上下文切换
Linux按照特权等级，把进程的运行空间分为内核空间和用户空间，分别对应着下图中， CPU 特权等级的 Ring 0 和 Ring 3。  
* 内核空间（Ring 0）具有最高权限，可以访问所有资源；  
* 用户空间（Ring 3）只能访问受限资源，不能直接访问内存等硬件设备，必须通过系统调用陷入到内核中，才能访问这些特权资源。  
![进程空间](https://static001.geekbang.org/resource/image/4d/a7/4d3f622f272c49132ecb9760310ce1a7.png)  
也就是说，进程既可以在用户空间运行，又可以在内核空间中运行。进程在用户空间运行时，被称为进程的用户态，而陷入内核空间的时候，被称为进程的内核态。  
从用户态到内核态的转变，需要通过系统调用来完成。比如，当我们查看文件内容时，就需要多次系统调用来完成：首先调用 open() 打开文件，然后调用 read() 读取文件内容，并调用 write() 将内容写到标准输出，最后再调用 close() 关闭文件。  
**系统调用的过程会发生CPU上下文的切换**。CPU 寄存器里原来用户态的指令位置，需要先保存起来。接着，为了执行内核态代码，CPU 寄存器需要更新为内核态指令的新位置。最后才是跳转到内核态运行内核任务。  
而系统调用结束后，CPU寄存器需要恢复原来保存的用户态，然后再切换到用户空间，继续运行进程。所以，**一次系统调用的过程，其实是发生了两次 CPU 上下文切换**。  

























