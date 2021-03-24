package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ConditionalOnBeanDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void test() {
        boolean userServiceByOnBean01 = applicationContext.containsBean("userServiceByOnBean01");
        Assertions.assertEquals(userServiceByOnBean01,false);

        boolean userServiceByOnBean02 = applicationContext.containsBean("userServiceByOnBean02");
        Assertions.assertEquals(userServiceByOnBean02,true);

        boolean userServiceByOnBean03 = applicationContext.containsBean("userServiceByOnBean03");
        Assertions.assertEquals(userServiceByOnBean03,true);
    }
}