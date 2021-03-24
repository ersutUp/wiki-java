package top.ersut.boot.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

@Configuration
public class ConditionalOnResourceDemo {

    //资源存在，bean成功注册
    @ConditionalOnResource(resources = "classpath:application.yml")
    @Bean
    public UserService userServiceByOnResource01(){
        return new UserServiceImpl();
    }

    //资源不存在,bean不注册
    @ConditionalOnResource(resources = "classpath:test.yml")
    @Bean
    public UserService userServiceByOnResource02(){
        return new UserServiceImpl();
    }

}
