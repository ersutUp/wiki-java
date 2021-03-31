package top.ersut.boot.yml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PeopleTest {

    @Autowired
    private People people;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void before() throws JsonProcessingException {
        Assertions.assertNotNull(people);
        System.out.println(objectMapper.writeValueAsString(people));
    }


    @Test
    void array(){
        Assertions.assertTrue(people.getBooks().contains("意志力"));
        Assertions.assertTrue(people.getBooks().contains("Thinking in java"));
    }

    @Test
    void keyValue(){
        /** yml 的 key值 对中文不友好 */
        Assertions.assertFalse(people.getTransport().keySet().contains("中文"));
        Assertions.assertTrue(people.getTransport().keySet().contains("bike"));
    }

    @Test
    void literals(){
        Assertions.assertEquals(people.getName(),"张\n三");
        Assertions.assertEquals(people.getAge(),18);
    }

    @Test
    void escapeCharacter(){
        System.out.println(people.getName());
        System.out.println(people.getAlias());
        System.out.println(people.getSex());
    }
}