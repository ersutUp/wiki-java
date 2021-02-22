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
2. 使用xml注入bean（DI操作）
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
3. 