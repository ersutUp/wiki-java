package top.ersut.boot.conditional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conditional.service.UserService;
import top.ersut.boot.conditional.service.impl.UserServiceImpl;

/**
 * 注意：{@link ConditionalOnMissingBean} 只验证当前容器中的Bean，未来注册的Bean不进行验证。
 *      也就是说下边的{@link #userServiceByOnMissingBean01}会进行注册， {@link #userServiceByOnMissingBean03} 不会进行注册
 */
@Configuration
public class ConditionalOnMissingBeanDemo {

    @ConditionalOnMissingBean(name = "userServiceByOnMissingBean02")
    @Bean
    public UserService userServiceByOnMissingBean01(){
        return new UserServiceImpl();
    }

    @Bean
    public UserService userServiceByOnMissingBean02(){
        return new UserServiceImpl();
    }

    @ConditionalOnMissingBean(name = "userServiceByOnMissingBean02")
    @Bean
    public UserService userServiceByOnMissingBean03(){
        return new UserServiceImpl();
    }
}
