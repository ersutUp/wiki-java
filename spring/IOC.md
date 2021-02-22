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
5. 


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