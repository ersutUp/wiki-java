package xyz.ersut.security.securitydemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('student')")
    public String hello(){
        return "hello";
    }

    @GetMapping("/hello/world")
    @PreAuthorize("@cex.hasAuthority('sys:.*:.*')")
    public String helloWorld(){
        return "hello world";
    }

    @GetMapping("/hello/human")
    @PreAuthorize("hasAuthority('sys:helloHuman:list')")
    public String helloHuman(){
        return "hello human";
    }

}
