package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import javax.annotation.PostConstruct;

class BeanDefaultScopeTest {

    @Test
    public void test(){
        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
        BeanDefaultScope beanDefaultScope1 = applicationContext.getBean("beanDefaultScope",BeanDefaultScope.class);
        BeanDefaultScope beanDefaultScope2 = applicationContext.getBean("beanDefaultScope",BeanDefaultScope.class);

        System.out.println("beanDefaultScope1:"+beanDefaultScope1);
        System.out.println("beanDefaultScope2:"+beanDefaultScope2);
        System.out.println("地址是否相等:"+(beanDefaultScope1==beanDefaultScope2));
    }

}