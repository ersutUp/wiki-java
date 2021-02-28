package top.ersut.spring.aop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import top.ersut.spring.config.ProjectConf;

class AdminTest {

    @Test
    void login() {

        ApplicationContext context = new AnnotationConfigApplicationContext(ProjectConf.class);
        Admin admin = context.getBean(Admin.class);

        System.out.println();

        Boolean isLogin = admin.login("wang");
        Assertions.assertTrue(isLogin);

        System.out.println();

        Admin.staticMethod();
    }
}