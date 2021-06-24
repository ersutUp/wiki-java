package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

class BeanDefaultScopeTest {

    @Test
    public void test(){
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ProjectConf.class);
        BeanDefaultScope beanDefaultScope1 =
                applicationContext.getBean(BeanDefaultScope.class);

        applicationContext.close();

        Assertions.assertTrue(beanDefaultScope1.callInit);
        Assertions.assertTrue(beanDefaultScope1.callDestroy);

    }

}