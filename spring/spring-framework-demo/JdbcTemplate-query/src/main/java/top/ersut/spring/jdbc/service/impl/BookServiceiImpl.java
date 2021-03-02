package top.ersut.spring.jdbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.pojo.BookConstructor;
import top.ersut.spring.jdbc.service.BookService;

import java.util.List;
import java.util.Map;

@Service
public class BookServiceiImpl implements BookService {

    @Autowired
    private BookDao bookDao;

    @Override
    public Integer queryCount() {
        return bookDao.selectCount();
    }

    @Override
    public Book queryById(Long id) {
        if(id == null){
            return null;
        }
        return bookDao.selectById(id);
    }

    /**
     * 根据id查询数据
     *
     * @param id
     */
    @Override
    public Map<String, Object> queryByIdToMap(Long id) {
        if(id == null){
            return null;
        }
        return bookDao.selectByIdToMap(id);
    }

    /** 查询某一行,返回到一个通过构造赋值的类 */
    @Override
    public BookConstructor queryByIdToConstructor(Long id){
        if(id == null){
            return null;
        }
        return bookDao.selectByIdToConstructor(id);
    }

    @Override
    public List<Book> queryAll() {
        return bookDao.selectAll();
    }

    @Override
    public Long saveBackId(String name, String price) {
        Book book = new Book();
        book.setbName(name);
        book.setPrice(price);
        return bookDao.insertBackId(book);
    }

}
