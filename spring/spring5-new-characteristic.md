# spring5 新特性

----------

## <div id="JUnit5"></div>JUnit5的整合

### 依赖包

```
<!-- 测试 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>${spring.version}</version>
</dependency>

<dependency>
	<groupId>org.junit.jupiter</groupId>
	<artifactId>junit-jupiter</artifactId>
	<version>RELEASE</version>
	<scope>test</scope>
</dependency>
```

### 使用 `@SpringJUnitConfig`

@SpringJUnitConfig是整合spring和junit的核心。

其属性value与classes指定配置类，locations属性指定xml配置文件。

示例：
```
//启动spring并加载配置文件
//@SpringJUnitConfig(classes = {SpringConf.class})
@SpringJUnitConfig({SpringConf.class})
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Test
    void query() {
        Long id = 1L;
        Book book = new Book();
        book.setId(id);
        book.setbName("意志力");
        book.setPrice("12.5");
        bookService.save(book);

        Book bookQuery = bookService.query(id);
        Assertions.assertEquals(book.getbName(),bookQuery.getbName(),"名称不一致");
        Assertions.assertEquals(book.getPrice(),bookQuery.getPrice(),"价格不一致");
    }
}
```


### [示例项目](./spring-framework-demo/newCharacteristic-JUnit5)
