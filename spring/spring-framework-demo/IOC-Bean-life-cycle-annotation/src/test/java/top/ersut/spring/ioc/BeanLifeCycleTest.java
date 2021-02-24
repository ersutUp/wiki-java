package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import top.ersut.spring.config.ProjectConf;

import static org.junit.jupiter.api.Assertions.*;

class BeanLifeCycleTest {

    @Test
    void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProjectConf.class);
        BeanLifeCycle beanLifeCycle = context.getBean(BeanLifeCycle.class);

        //销毁\关闭上下文
        context.close();
    }
}