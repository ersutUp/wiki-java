package top.ersut.boot.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import top.ersut.boot.filter.pojo.Room;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TypeExcludeFilterImplTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void studentBeanTest() {
        boolean flag = applicationContext.containsBean("student");
        Assertions.assertFalse(flag);

        Room room = applicationContext.getBean(Room.class);
        Assertions.assertNotNull(room);
    }
}