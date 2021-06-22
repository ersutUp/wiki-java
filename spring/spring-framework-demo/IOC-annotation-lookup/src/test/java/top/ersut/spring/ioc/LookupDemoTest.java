package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

import static org.junit.jupiter.api.Assertions.*;

class LookupDemoTest {

    @Test
    public void test(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        LookupDemo lookupDemo = applicationContext.getBean(LookupDemo.class);

        /**
         *
         * 原型模式的bean
         * 通过@Lookup注解标识这个方法需要spring重写，spring从BeanFactory调用getBean获取返回值的bean
         */
        Assertions.assertNotEquals(lookupDemo.getBeanScopePrototype(),lookupDemo.getBeanScopePrototype());
    }

}