package top.ersut.spring.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {"top.ersut.spring.transaction"})
//开启注解式事务
@EnableTransactionManagement
public class SpringConf {

    @Bean("druidDataSource")
    public DruidDataSource dataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        //设置数据库类型
        druidDataSource.setDbType("mysql");
        //设置数据库驱动
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        //设置数据地址
        druidDataSource.setUrl("jdbc:mysql://192.168.123.222/transaction_test?characterEncoding=utf8&allowMultiQueries=true");
        //数据库账号
        druidDataSource.setUsername("spring");
        //数据库密码
        druidDataSource.setPassword("spring");
        return druidDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("druidDataSource") DataSource dataSource){
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        //添加数据源
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    //事务
    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("druidDataSource") DataSource dataSource){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);
        return dataSourceTransactionManager;
    }

}
