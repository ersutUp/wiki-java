package xyz.ersut.security.securitydemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;

@RequestMapping("/api")
@RestController
public class ApiController {

    @GetMapping("/test")
//    @PreAuthorize("hasAuthority('openapi:test:list')")
    public ResultJson test(){
       return ResultJson.generateResultJson(ResultSystemCode.SUCCESS);
    }

}
