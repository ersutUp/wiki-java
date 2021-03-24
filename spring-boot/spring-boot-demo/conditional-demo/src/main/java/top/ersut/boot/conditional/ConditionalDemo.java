package top.ersut.boot.conditional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.condition.impl.ConditionalByProd;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

@Configuration
@Conditional(ConditionalByProd.class)
public class ConditionalDemo {

    @Bean
    public UserService userService(){
        return new UserServiceImpl();
    }

}
