package top.ersut.spring.jdbc.dao;

import top.ersut.spring.jdbc.pojo.Book;

import java.util.List;

public interface BookDao {

    /** 插入并返回主键 */
    Long insertBackId(Book book);

    /** 批量添加 */
    int[] batchInsert(List<Object[]> list);

    /** 批量更新 */
    int[] batchUpdate(List<Object[]> list);

    /** 批量删除 */
    int[] batchDelete(List<Object[]> list);

}
