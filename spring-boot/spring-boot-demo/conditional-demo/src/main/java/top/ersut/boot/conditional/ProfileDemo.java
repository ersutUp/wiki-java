package top.ersut.boot.conditional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;
@Configuration
@Profile("prod")
public class ProfileDemo {
    @Bean("userServiceByProfile")
    public UserService userService(){
        return new UserServiceImpl();
    }
}
