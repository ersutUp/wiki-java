# IOC

----------

## 什么是IOC
1. IOC即控制反转，一种思想
2. 将创建对象和对象之间的调用过程交给Spring管理
2. 使用IOC的目的是为了降低耦合

## 什么是Bean
项目中交给Spring的管理的类称之为Bean

## 什么是Bean管理
Bean管理指的两个操作
- spring创建对象
- spring注入属性

## 什么是DI
依赖注入，即bean管理中的注入属性

## IOC与DI的不同
DI是IOC中的注入属性的步骤，DI需要在创建对象的基础之上完成

## xml方式管理bean
1. 使用xml创建bean
	1. 在spring的xml配置文件中使用bean标签创建bean
	2. bean标签的必要属性
		1. id/name：bean的唯一标识，通过这个标识找到bean
		2. class：类的全路径（包名+类名）
	3. 创建bean依赖对应类的无参构造方法完成创建，若没有无参构造方法将抛出`java.lang.NoSuchMethodException`异常
	4. [示例项目](./spring-framework-demo/helloWorld)相关代码：`<bean id="hello" class="top.ersut.spring.Hello"></bean>`,
		1. 示例中bean的唯一标识为'hello'对应的类为'top.ersut.spring5.Hello'
2. 使用xml注入属性-字面量
	1. 通过setxxx方法注入
		1.  在spring的xml配置文件中使用bean标签的子标签property进行注入
		2.  property标签的属性
			1.  name：注入属性的名称
			2.  ref：引用现有的bean进行注入
			3.  value：直接赋值
		3.  注入属性依赖属性的set方法,如果没有方法将抛出`org.springframework.beans.NotWritablePropertyException`异常
		4.  [示例项目](./spring-framework-demo/IOC-DI-setxxx)相关代码：
		```
		<bean id="student" class="top.ersut.spring.ioc.Student">
		    <!--注入属性-->
		    <property name="name" value="wang"></property>
		</bean>
		```

	2. 通过有参构造方法注入
		1. 在spring的xml配置文件中使用bean标签的子标签constructor-arg进行注入
		2. constructor-arg常用属性：
			1. name：构造方法形参的名称
			2. ref：引用现有的bean进行注入
			3. value：直接赋值
		3. [示例项目](./spring-framework-demo/IOC-DI-constructor)相关代码：
		```
		<bean id="student" class="top.ersut.spring.ioc.Student">
		    <!--有参构造注入属性-->
		    <constructor-arg name="name" value="ersut"></constructor-arg>
		</bean>
		```
3. 使用xml注入属性-bean
	1. [示例项目](./spring-framework-demo/IOC-DI-setxxx-bean)相关代码：
		```
		<bean id="student" class="top.ersut.spring.ioc.Student">
		    <!--注入属性-->
		    <property name="teacher" ref="tea"></property>
		</bean>
		
		<bean id="tea" class="top.ersut.spring.ioc.Teacher"></bean>
		```

4. 使用xml注入属性-集合
	1. 集合对应的标签
		1. 在property标签的子标签内进行
		2. array标签对应数组
			1. 常用子标签：
				1. value：注入字面量值
				2. ref：注入bean
					1. 常用属性：
						1. bean:bean的唯一标识
			2. 代码示例：
			```
	        <property name="array">
	            <array>
	                <value>a</value>
	                <value>b</value>
	                <value>c</value>
	                <value>d</value>
	                <value>e</value>
	            </array>
	        </property>
			```
		3. list标签对应List集合(数组使用list标签也可以)
			1. 常用子标签：与array标签一致
			2. 代码示例：
			```
	        <property name="list">
	            <list>
	                <value>a</value>
	                <value>b</value>
	                <value>a</value>
	                <value>d</value>
	                <value>e</value>
	            </list>
	        </property>
			```
		4. set标签对应Set集合
			1. 常用子标签：与array标签一致
			2. 代码示例：
			```
	        <property name="set">
	            <list>
	                <value>a</value>
	                <value>b</value>
	                <value>c</value>
	                <value>e</value>
	                <value>e</value>
	            </list>
	        </property>
			```
		5. map标签对应Map集合
			1. 常用子标签：
				1. entry:给map注入值
					1. 常用属性
						1. key:字面量形式的key值
						2. value:字面量形式的value值
						3. ref-key:bean形式的key值
						4. ref-value:bean形式的value值
			2. 代码示例：
			```
	        <property name="map">
	            <map>
	                <entry key="97" value="a"></entry>
	                <entry key="98" value="b"></entry>
	                <entry key="99" value="c"></entry>
	                <entry key="100" value="d"></entry>
	                <entry key="101" value="e"></entry>
	            </map>
	        </property>
			```
	1. 字面量注入:以上示例均为字面量注入[示例项目](./spring-framework-demo/IOC-DI-collection)
	2. bean注入:
		1. 数组注入
			1. 代码示例
			```
	        <property name="array">
	            <array>
	                <ref bean="user1"></ref>
	                <ref bean="user2"></ref>
	                <ref bean="user1"></ref>
	            </array>
	        </property>
			```
		2. List注入
			1. 代码示例
			```
	        <property name="list">
	            <list>
	                <ref bean="user2"></ref>
	                <ref bean="user1"></ref>
	                <ref bean="user1"></ref>
	            </list>
	        </property>
			```
		3. Set注入
			1. 代码示例
			```
	        <property name="set">
	            <list>
	                <ref bean="user2"></ref>
	                <ref bean="user1"></ref>
	                <ref bean="user2"></ref>
	            </list>
	        </property>
			```
		4. Map注入
			1. 代码示例
			```
			<property name="map">
	            <map>
	                <entry key-ref="user1" value-ref="user2"></entry>
	                <entry key-ref="user2" value-ref="user1"></entry>
	            </map>
	        </property>
			```
		5. [示例项目](./spring-framework-demo/IOC-DI-collection-bean)
5. 引入外部配置
	1. 添加名称空间
	```
	<beans
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
	```
	2. 加载配置文件`<context:property-placeholder location="config.properties"/>`
		1. config.properties文件内容：
		```
		name=wang
		```
	3. 使用
	```
	<bean id="student" class="top.ersut.spring.ioc.Student">
        <!--注入属性-->
        <property name="name" value="${name}"></property>
    </bean>
	```
	4. [示例项目](./spring-framework-demo/xml-import)


## FactoryBean（工厂bean）
1. bean有两种：一种是普通bean(bean类型与引用类一致），一种是工厂bean（引用类与bean类型不一致）
2. 工厂bean可以定义一些复杂的bean，比如在不同的情况下返回不同的实现类
3. FactoryBean有3个方法
	1. boolean isSingleton()：是否为单利模式
	2. T getObject()：返回创建的Bean实例，如果isSingleton方法返回ture那么该实例会Spring容器的单实例缓存池中。
	3. Class<?> getObjectType()：返回该工厂创建bean的类型
4. [示例项目](./spring-framework-demo/IOC-FactoryBean),部分代码：
```
public class UserFactory implements FactoryBean<User> {

    private String impl;

    public void setImpl(String impl) {
        this.impl = impl;
    }

    @Override
    public User getObject() throws Exception {
        User user = null;

        if(Objects.equals(impl,"student")){
            user = new Student();
        } else if(Objects.equals(impl,"teacher")) {
            user = new Teacher();
        }

        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

## Bean作用域

1. Bean的作用域分为两种，一种是单实例，另一种是多实例
	1. 单实例是在加载配置文件时创建的bean，并放入bean单例缓存池
	2. 多实例是在获取bean时(getBean方法)创建的。
2. 默认情况下是单实例的。
	1. 示例代码：
		1. 配置文件：
		```
		<bean id="beanDefaultScope" class="top.ersut.spring.ioc.BeanDefaultScope"></bean>
		```
		2. 运行代码：
		```
	    @Test
	    public void test(){
	        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
	        BeanDefaultScope beanDefaultScope1 = applicationContext.getBean("beanDefaultScope",BeanDefaultScope.class);
	        BeanDefaultScope beanDefaultScope2 = applicationContext.getBean("beanDefaultScope",BeanDefaultScope.class);
	
	        System.out.println("beanDefaultScope1:"+beanDefaultScope1);
	        System.out.println("beanDefaultScope2:"+beanDefaultScope2);
	        System.out.println("地址是否相等:"+(beanDefaultScope1==beanDefaultScope2));
	    }
		```
		3. 运行结果：
		```
		beanDefaultScope1:top.ersut.spring.ioc.BeanDefaultScope@294425a7
		beanDefaultScope2:top.ersut.spring.ioc.BeanDefaultScope@294425a7
		地址是否相等:true
		```
3. 设置为多实例作用域
	1. 通过bean标签的scope属性设置
		1. 可选项：
			1. prototype：多实例
			2. singleton：单实例（默认值）
	3. 示例代码：
		1. 配置文件
		```
	    <!--多实例-->
	    <bean id="beanScopePrototype" class="top.ersut.spring.ioc.BeanScopePrototype" scope="prototype"></bean>
		```
		2. 运行代码
		```
	    @Test
	    public void test(){
	        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
	        BeanScopePrototype beanScopePrototype1 = applicationContext.getBean("beanScopePrototype",BeanScopePrototype.class);
	        BeanScopePrototype beanScopePrototype2 = applicationContext.getBean("beanScopePrototype",BeanScopePrototype.class);
	
	        System.out.println("beanScopePrototype1:"+beanScopePrototype1);
	        System.out.println("beanScopePrototype2:"+beanScopePrototype2);
	        System.out.println("地址是否相等:"+(beanScopePrototype1==beanScopePrototype2));
	    }
		```
		3. 运行结果
		```
		beanScopePrototype1:top.ersut.spring.ioc.BeanScopePrototype@9f116cc
		beanScopePrototype2:top.ersut.spring.ioc.BeanScopePrototype@12468a38
		地址是否相等:false
		```
4. [示例项目](./spring-framework-demo/IOC-Bean-scope)

## Bean的生命周期(xml配置版本)

### BeanPostProcessor介绍
BeanPostProcessor也称为Bean后置处理器，它是Spring中定义的接口，在**每个**Bean的创建过程中回调BeanPostProcessor中定义的两个方法。

源码如下：
```
public interface BeanPostProcessor {

	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
```

### Bean的生命周期
1. Bean实例化（调用类的构造方法）
2. 注入属性（DI）
3. 调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization方法）
4. 调用自定义初始化方法
5. 调用BeanPostProcessor的初始化后方法（postProcessAfterInitialization方法）
6. Bean可以使用了
7. 上下文销毁时，调用自定义销毁方法

### 配置bean的初始化方法 和 销毁方法
1. 配置初始化方法
	1. 设置bean标签的属性init-method，其值为方法名
	2. 示例`<bean id="beanLifeCycle" class="top.ersut.spring.ioc.BeanLifeCycle" init-method="init"/>`
2. 配置销毁方法
	1. 设置bean标签的属性destroy-method，其值为方法名
	2. 示例`<bean id="beanLifeCycle" class="top.ersut.spring.ioc.BeanLifeCycle" destroy-method="destroy"/>`

### <div id="xml-life-cycle"></div>[代码示例](./spring-framework-demo/IOC-Bean-life-cycle)
后置处理器：
```
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("3. 调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization方法）");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("5. 调用BeanPostProcessor的初始化后方法（postProcessAfterInitialization方法）");
        return bean;
    }
}
```

Bean对应的类：
```
public class BeanLifeCycle {

    public BeanLifeCycle() {
        System.out.println("1. Bean实例化（调用类的构造方法）");
    }

    private String str;

    public void setStr(String str) {
        System.out.println("2. 注入属性（DI）");
        this.str = str;
    }

    public void init(){
        System.out.println("4. 调用自定义初始化方法");
    }

    public void destroy(){
        System.out.println("7. 上下文销毁时，调用自定义销毁方法");
    }
}
```

spring配置：
```
<bean id="beanLifeCycle" class="top.ersut.spring.ioc.BeanLifeCycle" init-method="init" destroy-method="destroy">
    <property name="str" value="test"></property>
</bean>

<bean id="myBeanPostProcessor" class="top.ersut.spring.ioc.MyBeanPostProcessor"/>
```

测试方法：
```
@Test
void test() {
    GenericXmlApplicationContext context = new GenericXmlApplicationContext("bean.xml");
    BeanLifeCycle beanLifeCycle = context.getBean("beanLifeCycle",BeanLifeCycle.class);

    //销毁\关闭上下文
    context.close();
}
```

运行结果：
```
1. Bean实例化（调用类的构造方法）
2. 注入属性（DI）
3. 调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization方法）
4. 调用自定义初始化方法
5. 调用BeanPostProcessor的初始化后方法（postProcessAfterInitialization方法）
7. 上下文销毁时，调用自定义销毁方法
```

## 注解方式管理Bean

### 什么是注解
1. 注解用来标记代码，这个标记是可编译器、运行期被读取的。
3. 注解是JDK5引入的新特新
2. 格式：@注解名称[(key=value,...)]
4. 注解可以用在types（类、接口、枚举、注解类）、方法上、属性上，具体可以用在这三个那几个上是可控的
5. 示例：
	1. 示例1:单元测试
	```
	@Test
	void world() {
	}
	```
	2. 示例2:子类重写父类的方法
	```
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	}
	```

### 注解方式管理Bean的好处
1. 简化xml配置

### spring创建Bean的注解
1. @Component:适用于组件
2. @Repository:适用于持久层
3. @Service:适用于服务层
4. @Controller:适用于控制层

**以上4个注解功能一样，只是使用的地方不一样**

代码示例1：

	```
	//可以设置bean的名称为userServer2
	@Service("userServer2")
	public class UserServer {
	}
	```
代码示例2：

	```
	//如果不设置bean的名称，那么取类名将首字母小写作为Bean的名称，也就是userDao
	@Repository
	public class UserDao {
	}
	```

### 注解代替xml
使用@Configuration标记类使其成为配置类，这种方式是在代替xml配置文件

代码示例：
```
@Configuration
public class ProjectConfig {
}
```

### 创建Bean的注解生效
使用@ComponentScan注解进行配置使注解生效，**具体那些包下注解生效**需要用到 属性 basePackages,他接收一个String数组，也就是说他可以设置多个包,代码示例：

	```
	@Configuration
	@ComponentScan(basePackages = {"top.ersut.spring.ioc.dao","top.ersut.spring.ioc.server"})
	public class ProjectConfig {
	}
	```

### 自动注入bean的注解
1. @Autowired：根据属性的类型自动注入
	1. 可使用在属性上或者方法上
	2. 示例：根据类型注入
	```
    /**可以放在属性上*/
    @Autowired
    private UserDao userDao;
    
    private UserDao userDao2;
    @Autowired
    public void setUserDao2(UserDao userDao2) {
        this.userDao2 = userDao2;
    }
	```

	3. @Qualifier:这俩个配合使用可以根据Bean的名称进行自动注入
		1. 示例：根据Bean的名称注入
		```
	    @Autowired
	    @Qualifier("userDao")
	    private UserDao userDao3;
		```

2. @Resource:可以根据类型注入，也可以根据Bean的名称注入
	1. 示例1：根据类型注入
	```
    @Resource
    private UserDao userDao4;
	```
	2. 示例2：根据Bean的名称注入
	```
    @Resource(name = "userDao")
    private UserDao userDao5;
	```

3. @Value：普通属性注入值
	1. 示例：添加在属性上（也可以添加在set方法上此处不再做演示）
	```
    @Value("wang")
    private String name;
	```

### [示例项目](./spring-framework-demo/IOC-annotation),部分代码：
1. UserDao
	```
	@Repository
	public class UserDao {
	    @Value("wang")
	    private String name;
	
	    public String getName() {
	        System.out.println("UserDao.getName()");
	        return name;
	    }
	}
	```

1. UserServer
	```
	@Service("userServer2")
	public class UserServer {
		...	    
		public void selectUserName(){
	        userDao.getName();
	        userDao2.getName();
	        userDao3.getName();
	        userDao4.getName();
	        userDao5.getName();
	        System.out.println("UserServer.selectUserName");
	    }
	}
	```

2. 测试方法
	```
	@Test
    void selectUserName() {
        //实现类使用 AnnotationConfigApplicationContext
        ApplicationContext context = new AnnotationConfigApplicationContext(ProjectConfig.class);
        UserServer userServer = context.getBean("userServer2", UserServer.class);
        userServer.selectUserName();
    }
	```

4. 输出内容
	```
	UserDao.getName()
	UserDao.getName()
	UserDao.getName()
	UserDao.getName()
	UserDao.getName()
	UserServer.selectUserName
	```

### 使用注解将Bean设置为多例模式
在类上添加@Scope注解，其value属性设置为prototype

[示例项目(Bean的作用域注解版)](./spring-framework-demo/IOC-Bean-scope-annotation),关键代码：

```
@Component
@Scope("prototype")
public class BeanScopePrototype {
}

```

### 使用注解设置Bean的初始化方法和销毁方法
1. 初始化方法
	1. 在方法上添加@PostConstruct注解那么这个方法将变成初始化方法
	2. 代码示例：
	```
	@PostConstruct
    public void init(){
        System.out.println("4. 调用自定义初始化方法");
    }
	```
2. 摧毁方法
	1. 在方法上添加@PreDestroy注解那么这个方法将变成摧毁方法
	2. 代码示例：
	```
    @PreDestroy
    public void destroy(){
        System.out.println("7. 上下文销毁时，调用自定义销毁方法");
    }
	```
3. [示例项目(Bean的生命周期注解版)](./spring-framework-demo/IOC-Bean-life-cycle-annotation),部分代码：
	1. 配置类
	```
	@Configuration
	@ComponentScan(basePackages = "top.ersut.spring")
	public class ProjectConf {
	}
	```
	2. MyBeanPostProcessor
	```
	@Component
	public class MyBeanPostProcessor implements BeanPostProcessor {
	
	    @Override
	    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	        System.out.println("3. 调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization方法）");
	        return bean;
	    }
	
	    @Override
	    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	        System.out.println("5. 调用BeanPostProcessor的初始化后方法（postProcessAfterInitialization方法）");
	        return bean;
	    }
	}
	```
	3. 测试用例
	```
	@Test
    void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProjectConf.class);
        BeanLifeCycle beanLifeCycle = context.getBean(BeanLifeCycle.class);

        //销毁\关闭上下文
        context.close();
    }
	```
	4. 输出内容
	```
		3. 调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization方法）
		5. 调用BeanPostProcessor的初始化后方法（postProcessAfterInitialization方法）
		1. Bean实例化（调用类的构造方法）
		2. 注入属性（DI）
		3. 调用BeanPostProcessor的预初始化方法（postProcessBeforeInitialization方法）
		4. 调用自定义初始化方法
		5. 调用BeanPostProcessor的初始化后方法（postProcessAfterInitialization方法）
		7. 上下文销毁时，调用自定义销毁方法
	```
	5. 疑问:与[Bean的生命周期注解版](#xml-life-cycle)的输出结果相比,注解版的输出结果多了两行这是为什么
		1. 因为配置类也是一个bean所以在加载配置类的时候执行了一次后置处理器（MyBeanPostProcessor）