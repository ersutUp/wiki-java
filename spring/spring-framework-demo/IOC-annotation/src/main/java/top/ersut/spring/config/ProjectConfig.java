package top.ersut.spring.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"top.ersut.spring.ioc.dao","top.ersut.spring.ioc.server"})
public class ProjectConfig {
}
