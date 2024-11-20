package com.alibaba.csp.sentinel.demo.controller;

import com.alibaba.csp.sentinel.demo.service.Community;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlowDemoController {

    private final Community community;

    public FlowDemoController(Community community){
        this.community = community;
    }

    @GetMapping("/res")
    public String res() {
        return community.query("");
    }

    @GetMapping("/chain/{name}")
    public String chain(@PathVariable("name") String name) {
        return community.find(name);
    }

    @GetMapping("/limitApp")
    public String limit() {
        return community.limit();
    }

}
