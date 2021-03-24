package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ConditionalOnClassDemoTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void test(){
        boolean userServiceByOnClass01 = applicationContext.containsBean("userServiceByOnClass01");
        Assertions.assertEquals(userServiceByOnClass01,true);

        boolean userServiceByOnClass02 = applicationContext.containsBean("userServiceByOnClass02");
        Assertions.assertEquals(userServiceByOnClass02,true);

        boolean userServiceByOnClass03 = applicationContext.containsBean("userServiceByOnClass03");
        Assertions.assertEquals(userServiceByOnClass03,true);
    }

}