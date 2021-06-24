package top.ersut.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.ersut.spring.ioc.BeanDefaultScope;

@Configuration
@ComponentScan(basePackages = "top.ersut.spring")
public class ProjectConf {

    @Bean
    public BeanDefaultScope beanDefaultScope(){
        return new BeanDefaultScope();
    }

}
