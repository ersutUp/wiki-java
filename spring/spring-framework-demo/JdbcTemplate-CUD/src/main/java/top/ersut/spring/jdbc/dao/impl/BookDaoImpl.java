package top.ersut.spring.jdbc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;

@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 插入
     *
     * @param book
     */
    @Override
    public int insert(Book book) {
        String sql = "insert into book(b_name,price) values(?,?)";
        return jdbcTemplate.update(sql, book.getbName(), book.getPrice());
    }

    /**
     * 更新
     *
     * @param book
     */
    @Override
    public int update(Book book) {
        String sql = "update book set b_name=?,price=? where id=?";
        return jdbcTemplate.update(sql, book.getbName(), book.getPrice(),book.getId());
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public int delete(Long id) {
        String sql = "delete from book where id=?";
        return jdbcTemplate.update(sql, id);
    }
}
