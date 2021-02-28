package top.ersut.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"top.ersut.spring.aop"})
//开启Aop
@EnableAspectJAutoProxy
public class ProjectConf {
}
