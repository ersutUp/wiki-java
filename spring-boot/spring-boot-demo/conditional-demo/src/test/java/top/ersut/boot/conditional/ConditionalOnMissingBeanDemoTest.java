package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConditionalOnMissingBeanDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void test(){ 
        boolean userServiceByOnMissingBean01 = applicationContext.containsBean("userServiceByOnMissingBean01");
        Assertions.assertEquals(userServiceByOnMissingBean01,true);

        boolean userServiceByOnMissingBean02 = applicationContext.containsBean("userServiceByOnMissingBean02");
        Assertions.assertEquals(userServiceByOnMissingBean02,true);

        boolean userServiceByOnMissingBean03 = applicationContext.containsBean("userServiceByOnMissingBean03");
        Assertions.assertEquals(userServiceByOnMissingBean03,false);
    }
    
}