package top.ersut.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

//@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class},scanBasePackageClasses = {DelegatingWebMvcConfiguration.class,Application.class})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
