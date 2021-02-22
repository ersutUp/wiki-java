package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void say() {

        //创建上下文对象
        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
        //获取bean
        User student = applicationContext.getBean("student",User.class);
        //调用方法
        student.say();

        System.out.println("------------------------------------------");

        //获取bean
        User teacher = applicationContext.getBean("teacher",User.class);
        //调用方法
        teacher.say();

    }
}