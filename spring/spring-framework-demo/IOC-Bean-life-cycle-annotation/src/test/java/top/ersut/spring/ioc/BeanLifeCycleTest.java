package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

class BeanLifeCycleTest {

    @Test
    void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProjectConf.class);
        BeanLifeCycle beanLifeCycle = context.getBean(BeanLifeCycle.class);

        //销毁\关闭上下文
        context.close();
    }
}