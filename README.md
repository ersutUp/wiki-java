# java基础
- [类加载过程代码篇](./base/类加载过程代码篇.md)
- [查看内存中的字节码文件](./base/memory-class.md)
- [关于this关键字](./base/this.md)
- [（未查找原因）catch中抛出异常的同时finally中执行return,为什么抛出的这个异常无法捕获?](./base/catch-throw-finally-return.md)
- Lambda表达式与函数式编程

# spring5
- [新特性](./spring/spring5-new-characteristic.md)
	- [JUnit5的整合](./spring/spring5-new-characteristic.md#JUnit5)
	- [WebFlux](./spring/WebFlux.md)
		- [注解式编程模型](./spring/WebFlux.md#webFulx-annotation)
		- [函数式编程模型（未完成，欠缺基础）](./spring/WebFlux.md#webFulx-func)
		- [webClient（未完成，欠缺基础）](./spring/WebFlux.md#webFulx-client)
- [创建一个spring5项目](./spring/创建一个spring5项目.md)
- [IOC控制反转的使用](./spring/IOC.md)
	- [对IOC的理解](./spring/阅读官方文档后对IOC的理解.md)
	- [方法注入-@Lookup注解](./spring/IOC-@Lookup.md)
	- [bean 的作用域](./spring/IOC-bean-scope.md)
- [AOP面向切面编程](./spring/AOP.md)
	- [spring-aop的实现原理](./spring/AOP.md#spring-aop的实现原理)
	- [动态代理的两种方式](./spring/AOP.md#动态代理的两种方式)
	- [AOP专业术语](./spring/AOP.md#专业术语)
	- [aspectj](./spring/AOP.md#aspectj)
	- [Spring AOP示例](./spring/AOP.md#spring-aop示例)
	- [JDK动态代理、CGLIB动态代理、AspectJ 与 Spring AOP 之间的关系](./spring/AOP.md#jdk动态代理cglib动态代理aspectj-与-spring-aop-之间的关系)
- [jdbcTemplate的使用](./spring/jdbcTemplate.md)
- [事务](./spring/transaction.md)
	- [事务的四个特性](./spring/transaction.md#characteristic)
	- [编程式事务](./spring/transaction.md#program)
		- [命令式事务管理器中心接口类](./spring/transaction.md#PlatformTransactionManager)
	- [声明式事务](./spring/transaction.md#statement)
	- [事务的隔离性](./spring/transaction.md#Isolation)
	- [事务的传播行为](./spring/transaction.md#propagation)
- [【源码解析】DeferredImportSelector 使用以及声明周期](./spring/deferredImportSelector-souce.md)
-  [【源码解析】@EnableWebMvc & WebMvcConfigurer](./spring/【源码解析】@EnableWebMvc.md)


# spring boot 2
- [Spring Boot 介绍](./spring-boot/spring-boot.md)
- [boot 之 hello world](./spring-boot/boot-hello-world.md)
- [Spring Boot 的注解](./spring-boot/boot-annotation.md)
	- [@SpringBootTest](./spring-boot/boot-annotation.md#SpringBootTest)
	- [@ImportResource](./spring-boot/boot-annotation.md#ImportResource)
	- [@Configuration](./spring-boot/boot-annotation.md#Configuration)
	- [@Bean](./spring-boot/boot-annotation.md#Bean)
	- [@Conditional](./spring-boot/boot-annotation.md#Conditional)
	- [@Import](./spring-boot/boot-annotation.md#Import)
	- [@ConfigurationProperties](./spring-boot/boot-annotation.md#ConfigurationProperties)
- [Spring Boot 2 中 yaml 的使用](./spring-boot/yaml使用.md)
- [静态资源规则与定制化](./spring-boot/静态资源规则与定制化.md)
- [【源码解析】@SpringBootApplication 详解](./spring-boot/@SpringBootApplication详解.md)
- [spring-boot启动过程（简单分析）](./spring-boot/spring-boot启动过程（简单分析）.md)
- [spring-boot启动过程（源码分析）](./spring-boot/spring-boot启动过程（源码分析）.md)
- [WebMvcAutoConfiguration.EnableWebMvcConfiguration的作用](./spring-boot/EnableWebMvcConfiguration.md)

# web

- [浏览器的缓存机制](./web/浏览器的缓存机制.md)

# 日志



# Gson

- [JsonAdapter注解的使用](./java/JsonAdapter注解的使用.md)


# 书

- [Reactor 指南中文版](http://projectreactor.mydoc.io/)