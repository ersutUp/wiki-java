package top.ersut.spring.jdbc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

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
        //? 是占位符
        String sql = "insert into book(b_name,price) values(?,?)";
        return jdbcTemplate.update(sql, book.getbName(), book.getPrice());
    }


    @Override
    public Long insertBackId(Book book) {
        String sql = "insert into book(b_name,price) values(?,?)";

        PreparedStatementCreator preparedStatementCreator = con -> {
            //Statement.RETURN_GENERATED_KEYS 标识返回 id
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //设置占位符对应的值
            preparedStatement.setString(1,book.getbName());
            preparedStatement.setString(2,book.getPrice());
            return preparedStatement;
        };

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int row = jdbcTemplate.update(preparedStatementCreator,keyHolder);
        if (row > 0) {
            //返回的id
            return keyHolder.getKey().longValue();
        } else {
            return null;
        }
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
