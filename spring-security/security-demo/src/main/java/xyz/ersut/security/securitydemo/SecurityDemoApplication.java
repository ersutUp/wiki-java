package xyz.ersut.security.securitydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import xyz.ersut.security.securitydemo.pojo.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
//开启AOP
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class SecurityDemoApplication {

    //用户数据
    public static List<User> userAll = new ArrayList<User>(){
        {
            add(new User(1L,"root","$2a$10$/ZRxNNPOmWr8NgneZRA6EeE7O4.SRev9GvtttvbAc4tVrI4nUwt8.",1,18));//密码1234
            add(new User(2L,"test","$2a$10$w2ARsfOr0gQowgPW5P3U6OnjVjz4lBQy.P3NV9wWq1gDqGPcd0DqK",1,18));//密码12345
            add(new User(3L,"user","$2a$10$WqBtqXdaasqhyPNKsyPw9Oy4k3a9tyHKX4eLdnH2qJmzRR4m7u9AW",1,20));//密码123
            add(new User(4L,"admin","$2a$10$mWfqkVN2xv2dF8kSLyL.b.ss/OgcwC9h/Htz7N1u71hdt1fKyLNni",0,25));//密码123456
        }
    };
    //id索引
    public static Map<Long,User> userById = userAll.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    //username索引
    public static Map<String,User> userByUsername = userAll.stream().collect(Collectors.toMap(User::getUserName, Function.identity()));

    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoApplication.class, args);
    }

}
