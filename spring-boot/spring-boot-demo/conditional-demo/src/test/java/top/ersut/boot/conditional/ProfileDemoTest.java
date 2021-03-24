package top.ersut.boot.conditional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ProfileDemoTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void userService() {
        boolean userServiceByProfile = applicationContext.containsBean("userServiceByProfile");
        Assertions.assertEquals(userServiceByProfile,false);
    }
}