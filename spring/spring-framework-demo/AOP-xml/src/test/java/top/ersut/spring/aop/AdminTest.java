package top.ersut.spring.aop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

class AdminTest {

    @Test
    void login() {

        ApplicationContext context = new GenericXmlApplicationContext("spring.xml");
        Admin admin = context.getBean("admin",Admin.class);

        System.out.println();

        Boolean isLogin = admin.login("wang");
        Assertions.assertTrue(isLogin);

        System.out.println();

        Admin.staticMethod();
    }
}