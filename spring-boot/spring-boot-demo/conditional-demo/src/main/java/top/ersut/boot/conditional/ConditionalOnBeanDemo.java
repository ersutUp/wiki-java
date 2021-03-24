package top.ersut.boot.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

/**
 * 注意：{@link ConditionalOnBean} 只验证当前容器中的Bean，未来注册的Bean不进行验证。
 *      也就是说下边的{@link #userServiceByOnBean01}不会进行注册， {@link #userServiceByOnBean03} 会进行注册
 */
@Configuration
public class ConditionalOnBeanDemo {

    @ConditionalOnBean(name = "userServiceByOnBean02")
    @Bean
    public UserService userServiceByOnBean01(){
        return new UserServiceImpl();
    }

    @Bean
    public UserService userServiceByOnBean02(){
        return new UserServiceImpl();
    }

    @ConditionalOnBean(name = "userServiceByOnBean02")
    @Bean
    public UserService userServiceByOnBean03(){
        return new UserServiceImpl();
    }
}
