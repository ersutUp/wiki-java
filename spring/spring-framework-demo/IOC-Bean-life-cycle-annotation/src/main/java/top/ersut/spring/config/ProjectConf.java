package top.ersut.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.ersut.spring.ioc.MyBeanPostProcessor;

@Configuration
@ComponentScan(basePackages = "top.ersut.spring")
public class ProjectConf {
}
