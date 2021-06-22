package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

import static org.junit.jupiter.api.Assertions.*;

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