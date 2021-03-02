package top.ersut.spring.jdbc.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.SpringConf;

class BookServiceTest {

    BookService bookService;

    @BeforeEach
    void before(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConf.class);
        bookService = applicationContext.getBean(BookService.class);
    }

    @Test
    void save() {
        int row = bookService.save("意志力","42.00");
        Assertions.assertEquals(row,1);
    }

    @Test
    void saveBackId() {
        Long id = bookService.saveBackId("小狗钱钱","35.00");
        System.out.println("id:["+id+"]");
        Assertions.assertNotNull(id);
    }

    @Test
    void change() {
        Long id = bookService.saveBackId("小狗钱钱","35.00");
        Assertions.assertNotNull(id);
        int row = bookService.change(id,"小狗钱钱2","50.00");
        Assertions.assertEquals(row,1);
    }

    @Test
    void remove() {
        Long id = bookService.saveBackId("投资中最简单的事","55.00");
        Assertions.assertNotNull(id);
        int row = bookService.remove(id);
        Assertions.assertEquals(row,1);
    }

}