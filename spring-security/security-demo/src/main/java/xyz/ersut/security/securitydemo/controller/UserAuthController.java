package xyz.ersut.security.securitydemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.ersut.security.securitydemo.pojo.entity.User;
import xyz.ersut.security.securitydemo.service.UserService;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.Resultcode;

@RestController
public class UserAuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultJson login(@RequestBody User user){
        return userService.login(user);
    }

}
