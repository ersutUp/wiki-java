package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ConditionalOnPropertyDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void test(){
        boolean userServiceByOnProperty01 = applicationContext.containsBean("userServiceByOnProperty01");
        Assertions.assertEquals(userServiceByOnProperty01,false);

        boolean userServiceByOnProperty02 = applicationContext.containsBean("userServiceByOnProperty02");
        Assertions.assertEquals(userServiceByOnProperty02,true);

        boolean userServiceByOnProperty03 = applicationContext.containsBean("userServiceByOnProperty03");
        Assertions.assertEquals(userServiceByOnProperty03,false);

        boolean userServiceByOnProperty04 = applicationContext.containsBean("userServiceByOnProperty04");
        Assertions.assertEquals(userServiceByOnProperty04,true);
    }

}