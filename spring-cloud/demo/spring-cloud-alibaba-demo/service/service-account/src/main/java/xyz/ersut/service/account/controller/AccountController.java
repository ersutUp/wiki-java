package xyz.ersut.service.account.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @PutMapping("/topUp")
    public String topUp(int amount){
        return "ok";
    }

    @PutMapping("/pay")
    public String pay(int amount){
        return "pay ok";
    }

}
