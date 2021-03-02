package top.ersut.spring.jdbc.service;

import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.pojo.BookConstructor;

import java.util.List;
import java.util.Map;

public interface BookService {
    /** 统计行数 */
    Integer queryCount();

    /** 查询某一行 */
    Book queryById(Long id);

    /** 根据id查询数据 */
    Map<String,Object> queryByIdToMap(Long id);

    /** 查询某一行,返回到一个通过构造赋值的类 */
    BookConstructor queryByIdToConstructor(Long id);
    
    /** 查询所有数据 */
    List<Book> queryAll();

    Long saveBackId(String name,String price);
}
