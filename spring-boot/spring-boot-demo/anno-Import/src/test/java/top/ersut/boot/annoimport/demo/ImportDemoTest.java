package top.ersut.boot.annoimport.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import top.ersut.boot.annoimport.pojo.User;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ImportDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void Test(){
        boolean containsBean = applicationContext.containsBean("top.ersut.boot.annoimport.pojo.User");
        Assertions.assertEquals(containsBean,true);

        //获取某个类型的所有bean名称
        String[] userBeanNames = applicationContext.getBeanNamesForType(User.class);
        Assertions.assertNotEquals(userBeanNames.length,0);
        System.out.println(Arrays.toString(userBeanNames));
    }

}