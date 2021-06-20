# 对IOC的理解

1. ApplicationContext接口继承BeanFactory接口

2. BeanFactory接口提供了管理对象机制

3. ApplicationContext接口丰富了企业功能

4. ApplicationContext接口代表IOC容器

5. 容器通过读取配置元数据来实例化对象，配置元数据可以是xml文件（ClassPathXmlApplicationContext类等）、java注释（AnnotationConfigApplicationContext类）、java代码（GenericApplicationContext类）

6. 有了IOC容器不需要再显示的去new一个对象，IOC容器会通过配置元数据进行实例化所需的对象

7. IOC容器管理的对象称为bean

8. IOC容器内，这些 bean 定义表示为 BeanDefinition 对象

9. bean 的作用域：

  |      作用域 | 描述                                                         |
  | ----------: | ------------------------------------------------------------ |
  |   singleton | 单例模式，不管引用多少次都是同一个实例，默认值               |
  |   prototype | 原型模式，每次引用的都是不同的实例                           |
  |     request | 作用域限制在http请求的生命周期。也就是说每个请求的实例不同，但是一个请求内多次引用是同一个实例。**仅限于web环境内使用。**[测试用例](./spring-framework-demo/IOC-Bean-scope-web-annotation/src/test/java/top/ersut/spring/ioc/BeanScopeRequestTest.java) |
  |     session | 作用域限制在http中session的生命周期。**仅限于web环境内使用。**[测试用例](./spring-framework-demo/IOC-Bean-scope-web-annotation/src/test/java/top/ersut/spring/ioc/BeanScopeSessionTest.java) |
  | application | 作用域限制在`ServletContext`的生命周期。**仅限于web环境内使用。** |
  |   websocket | 作用域限制在websocket的生命周期。**仅限于web环境内使用。**   |

  **注意:**

  	1. prototype作用域下，spring不完全管理其生命周期，配置的摧毁方法不会被调用，[示例代码](./spring-framework-demo/IOC-Bean-scope-annotation/src/test/java/top/ersut/spring/ioc/BeanScopePrototypeTest.java)
   	2. session作用域下，实现的方式是将bean序列化存入session中，使用时在session中取出反序列化给bean，具体看下图

  ![](./images/ioc-scope-session.png)

10. 
