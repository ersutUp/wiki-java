package top.ersut.boot.conf.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudentServiceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void showNameBystudentService() {
        StudentService studentService = applicationContext.getBean("studentService",StudentService.class);
        Assertions.assertEquals(studentService.showName(),"studentService");
    }

    @Test
    void showNameBystudentService01() {
        StudentService studentService = applicationContext.getBean("studentService01",StudentService.class);
        Assertions.assertEquals(studentService.showName(),"studentService01");
    }

    @Test
    void showNameBystudentServiceXml() {
        StudentService studentService = applicationContext.getBean("studentServiceXml",StudentService.class);
        Assertions.assertEquals(studentService.showName(),"studentServiceXml");
    }
}