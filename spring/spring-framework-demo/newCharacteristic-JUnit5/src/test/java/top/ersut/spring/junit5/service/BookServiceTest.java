package top.ersut.spring.junit5.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import top.ersut.spring.config.SpringConf;
import top.ersut.spring.junit5.pojo.Book;

import static org.junit.jupiter.api.Assertions.*;

//启动spring并加载配置文件
//@SpringJUnitConfig(classes = {SpringConf.class})
@SpringJUnitConfig({SpringConf.class})
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void query() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setbName("意志力");
        book.setPrice("12.5");
        bookService.save(book);

        Book bookQuery = bookService.query(id);
        Assertions.assertEquals(book.getbName(),bookQuery.getbName(),"名称不一致");
        Assertions.assertEquals(book.getPrice(),bookQuery.getPrice(),"价格不一致");
    }
}