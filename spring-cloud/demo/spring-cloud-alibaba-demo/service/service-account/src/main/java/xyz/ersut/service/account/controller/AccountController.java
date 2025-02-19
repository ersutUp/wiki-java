package xyz.ersut.service.account.controller;

import com.alibaba.csp.sentinel.context.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    @PutMapping("/topUp")
    public String topUp(int amount){
        return "ok";
    }

    @PutMapping("/pay")
    public String pay(int amount){
        log.info("pay org:{}",ContextUtil.getContext().getOrigin());
        return "pay ok";
    }

}
