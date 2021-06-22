package top.ersut.spring.controller;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.ersut.spring.ioc.BeanScopeRequest;
import top.ersut.spring.ioc.BeanScopeSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

@RestController
public class MyController {


    @RequestMapping("request")
    public String request(BeanScopeRequest beanScopeRequest, HttpServletResponse response) throws IOException {
        return beanScopeRequest.toString();
    }
    @RequestMapping("session/{count}")
    public Integer session( HttpServletResponse response, HttpServletRequest request,@PathVariable Integer count) throws IOException {

        //存在了session内
        BeanScopeSession beanScopeSession = beanScopeSession();

        if(count == 1) {
            beanScopeSession.setNum(new Random().nextInt(100));
        }

        return beanScopeSession.getNum();
    }

    @Lookup
    public BeanScopeSession beanScopeSession(){
        return null;
    }

}
