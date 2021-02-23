package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

class StudentTest {

    @Test
    void say() {

        //创建上下文对象
        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
        //获取bean
        Student student = applicationContext.getBean("student",Student.class);
        //调用方法
        student.say();

    }
}