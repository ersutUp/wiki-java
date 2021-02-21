package top.ersut.valid.servlet_component_scan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@ServletComponentScan
@SpringBootApplication
public class ServletComponentScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServletComponentScanApplication.class, args);
    }

}


@RestController
class TestWeb{
    @RequestMapping("/")
    public String time(){
        return LocalTime.now().toString();
    }
}