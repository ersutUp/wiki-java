package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class BeanLifeCycleTest {

    @Test
    void test() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext("bean.xml");
        BeanLifeCycle beanLifeCycle = context.getBean("beanLifeCycle",BeanLifeCycle.class);

        //销毁\关闭上下文
        context.close();
    }
}