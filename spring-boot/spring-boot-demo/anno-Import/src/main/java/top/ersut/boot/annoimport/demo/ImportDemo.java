package top.ersut.boot.annoimport.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.ersut.boot.annoimport.pojo.User;

/**
 * 常规类导入，bean名称为类的全路径
 * 例如下方 User.class 的 Bean 名称为 top.ersut.boot.annoimport.pojo.User
 */
@Import(User.class)
@Configuration
public class ImportDemo {
}
