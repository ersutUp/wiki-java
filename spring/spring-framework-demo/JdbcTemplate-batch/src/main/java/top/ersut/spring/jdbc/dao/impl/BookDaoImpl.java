package top.ersut.spring.jdbc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class BookDaoImpl implements BookDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Override
    public int[] batchInsert(List<Object[]> list) {
        String sql = "insert into book(b_name,price) values(?,?)";
        //批量添加 参数2 使用list 其中每个object数组为一条数，据根据数组索引顺序来替换占位符
        return jdbcTemplate.batchUpdate(sql,list);
    }

    /**
     * 批量更新
     *
     * @param list
     */
    @Override
    public int[] batchUpdate(List<Object[]> list) {
        String sql = "update book set b_name = ?,price = ? where id = ?";
        //批量添加 参数2 使用list 其中每个object数组为一条数，据根据数组索引顺序来替换占位符
        return jdbcTemplate.batchUpdate(sql,list);
    }

    /**
     * 批量删除
     *
     * @param list
     */
    @Override
    public int[] batchDelete(List<Object[]> list) {
        String sql = "delete from book where id = ?";
        //批量添加 参数2 使用list 其中每个object数组为一条数，据根据数组索引顺序来替换占位符
        return jdbcTemplate.batchUpdate(sql,list);
    }
}
