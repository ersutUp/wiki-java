package top.ersut.spring.jdbc.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.SpringConf;
import top.ersut.spring.jdbc.service.impl.BookServiceiImpl;


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
    void change() {
        int row = bookService.change(1L,"意志力","40.00");
        Assertions.assertEquals(row,1);
    }

    @Test
    void remove() {
        int row = bookService.remove(1L);
        Assertions.assertEquals(row,1);
    }
}