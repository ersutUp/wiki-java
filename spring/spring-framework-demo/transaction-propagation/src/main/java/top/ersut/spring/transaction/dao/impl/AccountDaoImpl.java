package top.ersut.spring.transaction.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import top.ersut.spring.transaction.dao.AccountDao;
import top.ersut.spring.transaction.pojo.Account;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class AccountDaoImpl implements AccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Long insertBackId(Account account) {
        String sql = "insert into account(user_name,money) values(?,?)";

        PreparedStatementCreator preparedStatementCreator = con -> {
            //Statement.RETURN_GENERATED_KEYS 标识返回 id
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //设置占位符对应的值
            preparedStatement.setString(1,account.getUserName());
            preparedStatement.setLong(2,account.getMoney());
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
    public Integer deleteAll() {
        String sql = "delete from account";
        return jdbcTemplate.update(sql);
    }

    /**
     * 根据id查询
     *
     * @param id
     */
    @Override
    public Account selectById(Long id) {
        String sql = "select * from account where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<Account>(Account.class),id);
        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

}
