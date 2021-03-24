package top.ersut.boot.annoimport.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import top.ersut.boot.annoimport.pojo.Student;
import top.ersut.boot.annoimport.pojo.User;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ImportSelectorDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void Test(){
        boolean containsStudentBean = applicationContext.containsBean("top.ersut.boot.annoimport.pojo.Student");
        Assertions.assertEquals(containsStudentBean,true);

        boolean containsTeacherBean = applicationContext.containsBean("top.ersut.boot.annoimport.pojo.Teacher");
        Assertions.assertEquals(containsTeacherBean,true);
    }

}