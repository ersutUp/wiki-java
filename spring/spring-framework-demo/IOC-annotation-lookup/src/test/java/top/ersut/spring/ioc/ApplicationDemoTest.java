package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

import static org.junit.jupiter.api.Assertions.*;

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