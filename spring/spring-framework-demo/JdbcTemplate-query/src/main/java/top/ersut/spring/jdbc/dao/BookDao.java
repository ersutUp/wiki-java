package top.ersut.spring.jdbc.dao;

import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.pojo.BookConstructor;

import java.util.List;
import java.util.Map;

public interface BookDao {

    /** 插入并返回主键 */
    Long insertBackId(Book book);

    /** 统计行数 */
    Integer selectCount();

    /** 查询某一行,返回对象 */
    Book selectById(Long id);

    /** 查询某一行,返回map */
    Map<String,Object> selectByIdToMap(Long id);

    /** 查询某一行,返回到一个通过构造赋值的类 */
    BookConstructor selectByIdToConstructor(Long id);

    /** 查询所有数据 */
    List<Book> selectAll();

}
