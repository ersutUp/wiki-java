package com.alibaba.csp.sentinel.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class DegradeDemoController {

    private static final AtomicInteger slowAtomicInteger = new AtomicInteger();


    /**
     * 慢调用 熔断
     */
    @GetMapping("/degrade/slow")
    public String slow() throws InterruptedException {
        int count = slowAtomicInteger.addAndGet(1);
        //33%的慢调用
        if(count <= 3){
            Thread.sleep(1000);
        } else {

        }
        return "ok";
    }

    /**
     * 计数器清楚
     */
    @GetMapping("/degrade/clear")
    public String countclear() {
        slowAtomicInteger.set(0);
        return "ok";
    }




}
