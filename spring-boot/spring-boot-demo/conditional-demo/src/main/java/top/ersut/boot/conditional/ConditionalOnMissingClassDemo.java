package top.ersut.boot.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

@Configuration
public class ConditionalOnMissingClassDemo {

    //试着去掉 anno-conf 依赖再看看测试用例的结果
    @ConditionalOnMissingClass("top.ersut.boot.conf.service.StudentService")
    @Bean
    public UserService userServiceByOnMissingClass01(){
        return new UserServiceImpl();
    }

    //试着添加 hello-world 依赖再看看测试用例的结果
    @ConditionalOnMissingClass("top.ersut.boot.helloworld.controller.HelloWorldController")
    @Bean
    public UserService userServiceByOnMissingClass02(){
        return new UserServiceImpl();
    }

    @ConditionalOnMissingClass("top.ersut.boot.conditional.service.UserService")
    @Bean
    public UserService userServiceByOnMissingClass03(){
        return new UserServiceImpl();
    }
}
