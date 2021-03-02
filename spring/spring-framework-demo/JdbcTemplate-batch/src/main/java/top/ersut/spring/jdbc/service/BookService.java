package top.ersut.spring.jdbc.service;

import top.ersut.spring.jdbc.pojo.Book;

import java.util.List;
import java.util.Map;

public interface BookService {

    /** 插入并返回主键 */
    Long saveBackId(String name, String price);

    /** 批量添加 */
    int[] batchSave(Book... books);

    /** 批量更新 */
    int[] batchChange(Book... books);

    /** 批量删除 */
    int[] batchRemove(Long... ids);
}
