# @Lookup注解

### 使用范围

 `<public|protected> [abstract] <return-type> theMethodName(no-arguments);`

### 作用

由spring重写标识的方法，spring在容器中获取方法返回值的bean进行返回

### 应用场景

当单利bean内使用非单利bean时，如果通过`@Autowired`给非单例bean注入那么只有一次注入机会，这个时候就会发现非单例bean变成单利的了，解决这个问题就需要`@Lookup`。

### `@Autowired`注入示例（复现问题）

[`prototype`作用域的bean：](./spring-framework-demo/IOC-annotation-lookup/src/main/java/top/ersut/spring/ioc/BeanScopePrototype.java)

```java
@Component
@Scope("prototype")
public class BeanScopePrototype {
}
```

[通过`@Autowired`注入：](./spring-framework-demo/IOC-annotation-lookup/src/main/java/top/ersut/spring/ioc/AutowiredDemo.java)

```java
@Data
@Component
public class AutowiredDemo {
    @Autowired
    private BeanScopePrototype beanScopePrototype;
}
```

[测试用例：](./spring-framework-demo/IOC-annotation-lookup/src/test/java/top/ersut/spring/ioc/AutowiredDemoTest.java)

```java
class AutowiredDemoTest {
    @Test
    public void test(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        AutowiredDemo autowiredDemo = applicationContext.getBean(AutowiredDemo.class);

        /**
         * 原型模式的bean
         * 理想状态是每次调用返回不同的示例，但是通过测试好像也是单利模式的
         * 由于AutowiredDemo是单利模式的，他的属性只有一次注入机会所以产生了这种现象
         */
        Assertions.assertEquals(autowiredDemo.getBeanScopePrototype(),autowiredDemo.getBeanScopePrototype());
    }
}
```

### 通过注入`ApplicationContext`解决

[注入`ApplicationContext`：](./spring-framework-demo/IOC-annotation-lookup/src/main/java/top/ersut/spring/ioc/ApplicationDemo.java)

```java
@Data
@Component
public class ApplicationDemo {

    @Autowired
    private ApplicationContext applicationContext;

    public BeanScopePrototype getBeanScopePrototype(){
        return applicationContext.getBean(BeanScopePrototype.class);
    }

}
```

**通过`ApplicationContext#getBean`获取`BeanScopePrototype`**

[测试用例](./spring-framework-demo/IOC-annotation-lookup/src/test/java/top/ersut/spring/ioc/ApplicationDemoTest.java)

```
class ApplicationDemoTest {
    @Test
    public void test(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        ApplicationDemo applicationDemo = applicationContext.getBean(ApplicationDemo.class);

        /**
         * 原型模式的bean
         * 通过自动注入得到applicationContext容器,使用其方法获取BeanScopePrototype
         */
        Assertions.assertNotEquals(applicationDemo.getBeanScopePrototype(),applicationDemo.getBeanScopePrototype());
    }
}
```

以上方案需要注入`ApplicationContext`才能解决，那有没有更简单的方法？有，往下看

### 通过`@Lookup`来解决

[使用`@Lookup`：](./spring-framework-demo/IOC-annotation-lookup/src/main/java/top/ersut/spring/ioc/LookupDemo.java)

```java
@Component
public class LookupDemo {
    @Lookup
    public BeanScopePrototype getBeanScopePrototype(){
        return null;
    }
}
```

[测试用例：](./spring-framework-demo/IOC-annotation-lookup/src/test/java/top/ersut/spring/ioc/LookupDemoTest.java)

```java
class LookupDemoTest {
    @Test
    public void test(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        LookupDemo lookupDemo = applicationContext.getBean(LookupDemo.class);

        /**
         * 原型模式的bean
         * 通过@Lookup注解标识这个方法需要spring重写，spring从BeanFactory调用getBean获取返回值的bean
         */
        Assertions.assertNotEquals(lookupDemo.getBeanScopePrototype(),lookupDemo.getBeanScopePrototype());
    }
}
```

使用`@Lookup`更加简洁了。