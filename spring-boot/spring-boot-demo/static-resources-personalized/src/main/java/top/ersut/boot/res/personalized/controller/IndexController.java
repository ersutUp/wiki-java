package top.ersut.boot.res.personalized.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController {

    @RequestMapping
    public ModelAndView index(){
        System.out.println("index");
        return new ModelAndView("forward:/res/fg/test/test.html");
    }

}
