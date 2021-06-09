package top.ersut.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping
    public String index(){
        System.out.println("index");
        return "index";
    }

}
