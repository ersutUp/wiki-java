package top.ersut.spring.jdbc.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.SpringConf;
import top.ersut.spring.jdbc.pojo.Book;

import java.util.List;
import java.util.Map;

class BookServiceTest {

    BookService bookService;

    @BeforeEach
    void before(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConf.class);
        bookService = applicationContext.getBean(BookService.class);
        Assertions.assertNotNull(bookService);
    }


    @Test
    void batchSave() {
        Book book1 = new Book();
        book1.setbName("投资中最简单的事情");
        book1.setPrice("30.00");
        Book book2 = new Book();
        book2.setbName("小狗钱钱");
        book2.setPrice("30.00");

        int[] result = bookService.batchSave(book1,book2);

        Assertions.assertEquals(result.length,2,"添加数量错误");
    }


    @Test
    void batchChange() {
        //添加两条数据
        Long id1 = bookService.saveBackId("投资中最简单的事情(更新版)","35.00");
        Assertions.assertNotNull(id1);
        Long id2 = bookService.saveBackId("小狗钱钱2","50.00");
        Assertions.assertNotNull(id2);

        //组装更新数据
        Book book1 = new Book();
        book1.setbName("投资中最简单的事情");
        book1.setPrice("30.00");
        book1.setId(id1);

        Book book2 = new Book();
        book2.setbName("小狗钱钱");
        book2.setPrice("30.00");
        book2.setId(id2);

        //更新数据
        int[] result = bookService.batchChange(book1,book2);

        Assertions.assertEquals(result.length,2,"修改数量错误");
    }

    @Test
    void batchRemove() {
        //添加两条数据
        Long id1 = bookService.saveBackId("投资中最简单的事情","35.00");
        Assertions.assertNotNull(id1);
        Long id2 = bookService.saveBackId("小狗钱钱","50.00");
        Assertions.assertNotNull(id2);

        //删除数据
        int[] result = bookService.batchRemove(id1,id2);
        Assertions.assertEquals(result.length,2,"删除数量错误");
    }
}