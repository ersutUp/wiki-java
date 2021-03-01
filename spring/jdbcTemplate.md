# jdbcTemplate 使用

----------

## 什么是JdbcTemplate
JdbcTemplate 是spring对jdbc的封装，简化了jdbc的繁琐操作。

## 依赖包
1. 使用mysql数据库
2. Druid框架作为连接池

具体依赖包

```
<!-- jdbc -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring.version}</version>
</dependency>
<!-- 事务 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>${spring.version}</version>
</dependency>

<!-- mysql -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>${mysql.version}</version>
</dependency>

<!-- druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>${druid.version}</version>
</dependency>

<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-beans</artifactId>
	<version>${spring.version}</version>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-core</artifactId>
	<version>${spring.version}</version>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-context</artifactId>
	<version>${spring.version}</version>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-expression</artifactId>
	<version>${spring.version}</version>
</dependency>
```

## spring-jdbc 与 spring-orm 的区别

- spring-jdbc是纯jdbc的封装
- spring-orm是制定了规则，供第三方持久化框架集成到spring。例如jpa、hibernate

## 配置Druid连接池

```
@Bean("druidDataSource")
public DruidDataSource dataSource(){
    DruidDataSource druidDataSource = new DruidDataSource();
    //设置数据库类型
    druidDataSource.setDbType("mysql");
    //设置数据库驱动
    druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
    //设置数据地址
    druidDataSource.setUrl("jdbc:mysql://192.168.123.222/jdbc_test?characterEncoding=utf8&allowMultiQueries=true");
    //数据库账号
    druidDataSource.setUsername("jdbc");
    //数据库密码
    druidDataSource.setPassword("jdbc");
    return druidDataSource;
}
```

**@Bean注解是创建一个Bean,相当于xml的bean标签**

## 配置JdbcTemplate

```
@Bean
public JdbcTemplate jdbcTemplate(@Qualifier("druidDataSource")DataSource dataSource){
    JdbcTemplate jdbcTemplate = new JdbcTemplate();
    //添加数据源
    jdbcTemplate.setDataSource(dataSource);
    return jdbcTemplate;
}
```

## 使用JdbcTemplate进行增删改

### [示例项目](./spring-framework-demo/JdbcTemplate-CUD)