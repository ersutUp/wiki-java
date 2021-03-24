package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ConditionalOnMissingClassDemoTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Test
    void test(){
        boolean userServiceByOnMissingClass01 = applicationContext.containsBean("userServiceByOnMissingClass01");
        Assertions.assertEquals(userServiceByOnMissingClass01,false);

        boolean userServiceByOnMissingClass02 = applicationContext.containsBean("userServiceByOnMissingClass02");
        Assertions.assertEquals(userServiceByOnMissingClass02,true);

        boolean userServiceByOnMissingClass03 = applicationContext.containsBean("userServiceByOnMissingClass03");
        Assertions.assertEquals(userServiceByOnMissingClass03,false);
    }
    
}