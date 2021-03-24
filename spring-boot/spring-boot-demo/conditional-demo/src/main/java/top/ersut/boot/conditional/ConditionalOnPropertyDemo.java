package top.ersut.boot.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

@Configuration
public class ConditionalOnPropertyDemo {

    //属性值存在 预期值不匹配，bean无法注册
    @ConditionalOnProperty(prefix = "spring.profiles",name = "active",havingValue = "prod")
    @Bean
    public UserService userServiceByOnProperty01(){
        return new UserServiceImpl();
    }

    //属性值存在 预期值匹配，bean成功注册
    @ConditionalOnProperty(prefix = "spring.profiles",name = "active",havingValue = "dev")
    @Bean
    public UserService userServiceByOnProperty02(){
        return new UserServiceImpl();
    }

    //属性值不存在 默认bean不注册
    @ConditionalOnProperty(prefix = "spring.profiles",name = "include",havingValue = "dev-jdbc")
    @Bean
    public UserService userServiceByOnProperty03(){
        return new UserServiceImpl();
    }

    //属性值不存在 bean注册
    @ConditionalOnProperty(prefix = "spring.profiles",name = "include",havingValue = "dev-jdbc",matchIfMissing = true)
    @Bean
    public UserService userServiceByOnProperty04(){
        return new UserServiceImpl();
    }
}
