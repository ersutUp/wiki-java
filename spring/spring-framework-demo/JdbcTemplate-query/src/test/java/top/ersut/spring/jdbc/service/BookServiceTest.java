package top.ersut.spring.jdbc.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.ersut.spring.config.SpringConf;
import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.pojo.BookConstructor;

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
    void queryCount() {
        Integer count = bookService.queryCount();
        Assertions.assertNotNull(count);
    }

    @Test
    void queryById() {
        String name = "小狗钱钱";
        String price = "35.00";
        Long id = bookService.saveBackId(name,price);
        Assertions.assertNotNull(id);

        //查询数据
        Book book = bookService.queryById(id);
        Assertions.assertEquals(book.getbName(),name,"书名不一致");
        Assertions.assertEquals(book.getPrice(),price,"价钱不一致");
    }

    @Test
    void queryAll() {
        //统计条数
        Integer count = bookService.queryCount();
        //查询所有数据
        List<Book> bookList = bookService.queryAll();
        Assertions.assertEquals(bookList.size(),count,"数据个数不一致");
    }

    @Test
    void queryByIdToMap() {
        String name = "意志力";
        String price = "30.00";
        Long id = bookService.saveBackId(name,price);
        Assertions.assertNotNull(id);

        //查询数据
        Map<String, Object> bookMap = bookService.queryByIdToMap(id);
        Assertions.assertEquals(bookMap.get("b_name"),name,"书名不一致");
        Assertions.assertEquals(bookMap.get("price"),price,"价钱不一致");
    }

    @Test
    void queryByIdToPublic() {
        String name = "意志力";
        String price = "30.00";
        Long id = bookService.saveBackId(name,price);
        Assertions.assertNotNull(id);

        //查询数据
        BookConstructor bookConstructor = bookService.queryByIdToConstructor(id);
        Assertions.assertEquals(bookConstructor.getbName(),name,"书名不一致");
        Assertions.assertEquals(bookConstructor.getPrice(),price,"价钱不一致");
    }

}