package top.ersut.spring.jdbc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import top.ersut.spring.jdbc.dao.BookDao;
import top.ersut.spring.jdbc.pojo.Book;
import top.ersut.spring.jdbc.pojo.BookConstructor;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public Integer selectCount() {
        //统计行数
        String sql = "select count(1) from book";
        //查询返回值
        return jdbcTemplate.queryForObject(sql,Integer.class);
    }

    @Override
    public Book selectById(Long id) {
        //根据id查询 sql
        String sql = "select * from book where id = ?";
        //查询返回对象
        return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<Book>(Book.class),id);
    }

    /**
     * 查询某一行,返回map
     *
     * @param id
     */
    @Override
    public Map<String, Object> selectByIdToMap(Long id) {
        //根据id查询 sql
        String sql = "select * from book where id = ?";
        //查询返回 Map
        return jdbcTemplate.queryForObject(sql,new ColumnMapRowMapper(),id);
    }

    /**
     * 查询某一行,返回到一个通过构造赋值的类
     *
     * @param id
     */
    @Override
    public BookConstructor selectByIdToConstructor(Long id) {
        //根据id查询 sql
        String sql = "select * from book where id = ?";

        //查询返回对象（DataClassRowMapper通过构造赋值）
        BookConstructor bookPublic = jdbcTemplate.queryForObject(sql,new DataClassRowMapper<BookConstructor>(BookConstructor.class),id);

        //DataClassRowMapper依旧支持通过 setter 方法设值
        Book book = jdbcTemplate.queryForObject(sql,new DataClassRowMapper<Book>(Book.class),id);

        return bookPublic;
    }

    @Override
    public List<Book> selectAll() {
        String sql = "select * from book";
        //查询返回对象集合
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<Book>(Book.class));
    }

}
