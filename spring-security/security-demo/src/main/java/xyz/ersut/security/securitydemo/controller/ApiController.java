package xyz.ersut.security.securitydemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;

import java.util.Map;

@RequestMapping("/api")
@RestController
public class ApiController {

    @RequestMapping("/test")
//    @PreAuthorize("hasAuthority('openapi:test:list')")
    public ResultJson test(@RequestBody(required = false) Object body, @RequestParam(required = false) Object a){
        return ResultJson.generateResultJson(ResultSystemCode.SUCCESS);
    }

}
