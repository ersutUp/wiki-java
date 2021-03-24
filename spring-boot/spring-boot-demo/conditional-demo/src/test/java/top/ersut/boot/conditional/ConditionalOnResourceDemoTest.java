package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConditionalOnResourceDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void test(){

        boolean userServiceByOnResource01 = applicationContext.containsBean("userServiceByOnResource01");
        Assertions.assertEquals(userServiceByOnResource01,true);

        boolean userServiceByOnResource02 = applicationContext.containsBean("userServiceByOnResource02");
        Assertions.assertEquals(userServiceByOnResource02,false);

    }

}
