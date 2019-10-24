### 1.Bean的生命周期？
以下是一个Bean的构造过程：  
![Bean的构造过程](https://www.javazhiyin.com/wp-content/uploads/2019/05/java0-1558500658.jpg)  
（1）Spring启动，查找并加载Bean，进行Bean的实例化；  
（2）Bean实例化后，对Bean的引入和值注入到Bean的属性中；  
（3）如果Bean实现了BeanNameAware接口，Spring将Bean的Id传递给setBeanName()方法；  
（4）如果Bean实现了BeanFactoryAware接口，Spring调用setBeanFactory()方法，将BeanFactory容器实例传入；  
（5）如果Bean实现了ApplicationContextAware接口的话，Spring将调用Bean的setApplicationContext()方法，将bean所在应用上下文引用传入进来；  
（6）如果Bean实现了BeanPostProcessor接口，Spring就将调用他们的postProcessBeforeInitialization()方法；  
（7）如果Bean 实现了InitializingBean接口，Spring将调用他们的afterPropertiesSet()方法。类似的，如果bean使用init-method声明了初始化方法，
该方法也会被调用；  
（8）如果Bean实现了BeanPostProcessor接口，Spring就将调用他们的postProcessAfterInitialization()方法；  
（9）此时，Bean已经准备就绪，可以被应用程序使用了。他们将一直驻留在应用上下文中，直到应用上下文被销毁；  
（10）如果bean实现了DisposableBean接口，Spring将调用它的destory()接口方法，同样，如果bean使用了destory-method 声明销毁方法，该方法也会被调用。  

### 2.Bean的作用域
（1）Singleton：唯一Bean实例，Spring中的bean默认都是单例的；  
（2）prototype：每次请求都会创建一个新的Bean实例；  
（3）request：每次HTTP请求都会产生一个新的bean，该bean仅在当前HTTP request内有效；  
（4）session：每次HTTP请求都会产生一个新的bean，该bean仅在当前HTTP session内有效；  
（5）global-session：全局session作用域，仅仅在基于portlet的web应用中才有意义，Spring5已经没有了。
