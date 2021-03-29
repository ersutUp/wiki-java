package top.ersut.boot.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import top.ersut.boot.application.pojo.Admin;
import top.ersut.boot.scan.clazz.Stutdent;

@SpringBootApplication(
        scanBasePackages = "top.ersut.boot.scan.path"
        ,scanBasePackageClasses = Stutdent.class
        ,exclude = ConfigurationPropertiesAutoConfiguration.class
        ,excludeName = "org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration"
        ,proxyBeanMethods = false)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Admin admin(){
        return new Admin();
    }

}
