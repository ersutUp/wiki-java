package top.ersut.spring.jdbc.dao;

import top.ersut.spring.jdbc.pojo.Book;

public interface BookDao {

    /** 插入 */
    int insert(Book book);

    /** 更新 */
    int update(Book book);

    /** 删除 */
    int delete(Long id);
}
