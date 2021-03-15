package top.ersut.boot.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.ersut.boot.conf.service.StudentService;
import top.ersut.boot.conf.service.impl.StudentServiceImpl;

@Configuration
public class ProjectConf {

    //使用方法名作为bean的名称
    @Bean
    public StudentService studentService(){
        return new StudentServiceImpl("studentService");
    }

    //指定bean的名称
    @Bean("studentService01")
    public StudentService studentServiceTow(){
        return new StudentServiceImpl("studentService01");
    }

}
