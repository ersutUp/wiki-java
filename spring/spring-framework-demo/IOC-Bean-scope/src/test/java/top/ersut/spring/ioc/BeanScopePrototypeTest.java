package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class BeanScopePrototypeTest {

    @Test
    public void test(){
        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
        BeanScopePrototype beanScopePrototype1 = applicationContext.getBean("beanScopePrototype",BeanScopePrototype.class);
        BeanScopePrototype beanScopePrototype2 = applicationContext.getBean("beanScopePrototype",BeanScopePrototype.class);

        System.out.println("beanScopePrototype1:"+beanScopePrototype1);
        System.out.println("beanScopePrototype2:"+beanScopePrototype2);
        System.out.println("地址是否相等:"+(beanScopePrototype1==beanScopePrototype2));
    }
    
}