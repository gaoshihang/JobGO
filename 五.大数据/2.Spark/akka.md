akka是一个用来开发分布式底层通信体系和消息传输机制的框架。它同时支持并发编程、异步编程以及分布式编程，提供了java和scala两种编程语言接口，且非常简单易用。  

Akka使用称为Actor的编程模型，这是一种和面向对象编程模型平行的一种编程模型。面向对象认为一切都是对象，对象之间通过消息传递，也就是方法调用实现复杂功能。  
Actor模型认为一切都是Actor，Actor之间也通过消息传递实现复杂的功能，但是这里的消息是真正意义上的消息。**不同于面向对象模型，方法调用是同步阻塞的，也就是被
调用者在处理完成之前，调用者必须阻塞等待；给Actor发送消息不需要等待Actor处理，是异步的，发送完就不用管了**。  

Actor编程模型可以很好的利用多核CPU与分布式特性，轻松实现并发、异步、分布式编程。  
以下是一个Actor的例子：  
```
class MyActor extends Actor{
  val log = Logging(context.system, this)
  
  def receive = {
    case "test" => log.info("received test")
    case _ => log.info("received unknown message")
  }
}
```
**一个Actor类最重要的就是实现receive方法**。在receive根据接收的消息进行对应的处理。而Actor之间互相发送消息，协作完成复杂的计算操作。  

这种全部消息都是异步，通过异步消息完成业务处理的编程方式也叫做**响应式编程**，Akka的Actor编程就是响应式编程的一种。  

**Akka实现异步消息的原理是**：Actor之间的消息传输通过一个收件箱Mailbox完成，发送者Actor的消息发到接收者Actor的收件箱，接收者Actor一个一个串行取消息调用
自己的receive方法进行处理。  






