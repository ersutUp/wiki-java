package top.ersut.spring.jdbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.service.BookService;

@Service
public class BookServiceiImpl implements BookService {

    @Autowired
    private BookDao bookDao;

    @Override
    public int save(String name,String price) {
        Book book = new Book();
        book.setbName(name);
        book.setPrice(price);
        return bookDao.insert(book);
    }

    @Override
    public Long saveBackId(String name, String price) {
        Book book = new Book();
        book.setbName(name);
        book.setPrice(price);
        return bookDao.insertBackId(book);
    }

    @Override
    public int change(Long id,String name,String price) {
        Book book = new Book();
        book.setId(id);
        book.setbName(name);
        book.setPrice(price);
        return bookDao.update(book);
    }

    @Override
    public int remove(Long id) {
        return bookDao.delete(id);
    }
}
