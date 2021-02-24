package top.ersut.spring.ioc.server;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.ProjectConfig;
import top.ersut.spring.ioc.dao.UserDao;

import static org.junit.jupiter.api.Assertions.*;

class UserServerTest {

    @Test
    void selectUserName() {
        //实现类使用 AnnotationConfigApplicationContext
        ApplicationContext context = new AnnotationConfigApplicationContext(ProjectConfig.class);
        UserServer userServer = context.getBean("userServer2", UserServer.class);
        userServer.selectUserName();
    }
}