package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

class BeanDefaultScopeTest {

    @Test
    public void test(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        BeanDefaultScope beanDefaultScope1 = applicationContext.getBean(BeanDefaultScope.class);
        BeanDefaultScope beanDefaultScope2 = applicationContext.getBean(BeanDefaultScope.class);

        System.out.println("beanDefaultScope1:"+beanDefaultScope1);
        System.out.println("beanDefaultScope2:"+beanDefaultScope2);
        System.out.println("地址是否相等:"+(beanDefaultScope1==beanDefaultScope2));
        applicationContext.close();
        Assertions.assertTrue(beanDefaultScope1.callInit);
        Assertions.assertTrue(beanDefaultScope2.callInit);
        Assertions.assertTrue(beanDefaultScope1.callDestroy);
        Assertions.assertTrue(beanDefaultScope2.callDestroy);
    }

}