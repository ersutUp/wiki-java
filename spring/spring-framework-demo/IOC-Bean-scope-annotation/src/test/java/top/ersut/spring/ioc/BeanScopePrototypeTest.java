package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConf;

class BeanScopePrototypeTest {

    @Test
    public void test(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        BeanScopePrototype beanScopePrototype1 = applicationContext.getBean(BeanScopePrototype.class);
        BeanScopePrototype beanScopePrototype2 = applicationContext.getBean(BeanScopePrototype.class);

        System.out.println("beanScopePrototype1:"+beanScopePrototype1);
        System.out.println("beanScopePrototype2:"+beanScopePrototype2);
        System.out.println("地址是否相等:"+(beanScopePrototype1==beanScopePrototype2));
        applicationContext.close();
        Assertions.assertTrue(beanScopePrototype1.callInit);
        Assertions.assertTrue(beanScopePrototype2.callInit);
        Assertions.assertFalse(beanScopePrototype1.callDestroy);
        Assertions.assertFalse(beanScopePrototype2.callDestroy);
    }
    
}