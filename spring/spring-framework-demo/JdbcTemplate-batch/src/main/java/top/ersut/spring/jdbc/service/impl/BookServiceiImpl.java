package top.ersut.spring.jdbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.service.BookService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceiImpl implements BookService {

    @Autowired
    private BookDao bookDao;

    @Override
    public Long saveBackId(String name, String price) {
        Book book = new Book();
        book.setbName(name);
        book.setPrice(price);
        return bookDao.insertBackId(book);
    }

    @Override
    public int[] batchSave(Book... books) {
        if (books.length <= 0){
            return null;
        }
        //改变为List<Object[])
        List<Object[]> list = new ArrayList<>();
        for (Book book : books) {
            Object[] object = new Object[2];
            object[0] = book.getbName();
            object[1] = book.getPrice();
            list.add(object);
        }

        return bookDao.batchInsert(list);
    }

    /**
     * 批量更新
     */
    @Override
    public int[] batchChange(Book... books) {
        if (books.length <= 0){
            return null;
        }

        //改变为List<Object[])
        List<Object[]> list = new ArrayList<>();
        for (Book book : books) {
            Object[] object = new Object[3];
            object[0] = book.getbName();
            object[1] = book.getPrice();
            object[2] = book.getId();
            list.add(object);
        }


        return bookDao.batchUpdate(list);
    }

    /**
     * 批量删除
     */
    @Override
    public int[] batchRemove(Long... ids) {
        if (ids.length <= 0){
            return null;
        }

        //改变为List<Object[])
        List<Object[]> list = new ArrayList<>();
        for (Long id : ids) {
            Object[] object = new Object[1];
            object[0] = id;
            list.add(object);
        }

        return bookDao.batchDelete(list);
    }
}
