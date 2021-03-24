package top.ersut.boot.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

@Configuration
public class ConditionalOnClassDemo {

    //试着去掉 anno-conf 依赖再看看测试用例的结果
    @ConditionalOnClass(name = "top.ersut.boot.conf.service.StudentService")
    @Bean
    public UserService userServiceByOnClass01(){
        return new UserServiceImpl();
    }

    @Bean
    public UserService userServiceByOnClass02(){
        return new UserServiceImpl();
    }

    @ConditionalOnClass(name = "top.ersut.boot.conditional.service.UserService")
    @Bean
    public UserService userServiceByOnClass03(){
        return new UserServiceImpl();
    }
}
