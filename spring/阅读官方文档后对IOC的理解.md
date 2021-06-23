# 对IOC的理解

1. ApplicationContext接口继承BeanFactory接口
2. BeanFactory接口提供了管理对象机制
3. ApplicationContext接口丰富了企业功能
4. ApplicationContext接口代表IOC容器
5. 容器通过读取配置元数据来实例化对象，配置元数据可以是xml文件（ClassPathXmlApplicationContext类等）、java注释（AnnotationConfigApplicationContext类）、java代码（GenericApplicationContext类）
6. 有了IOC容器不需要再显示的去new一个对象，IOC容器会通过配置元数据进行实例化所需的对象
7. IOC容器管理的对象称为bean
8. IOC容器内，这些 bean 定义表示为 BeanDefinition 对象
9. 
