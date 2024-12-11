package xyz.ersut.service.order.controller;

import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.ersut.service.account.client.RemoteAccountService;
import xyz.ersut.service.account.client.RemoteBonusService;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private RemoteAccountService remoteAccountService;

    @Autowired
    private RemoteBonusService remoteBonusService;

    @PostMapping
    public String createOrder( int id, int number){
        String origin = ContextUtil.getContext().getOrigin();
        log.info("origin:{}",origin);
        String payInfo = remoteAccountService.pay(2);
        log.info("id:{},num:{}",id,number);
        String result = "accountService:"+ payInfo +";\nmy:ok";
        return result;
    }

    @DeleteMapping("/del")
    public String delOrder(int id){
        String origin = ContextUtil.getContext().getOrigin();
        log.info("del origin:{}",origin);
        String promotionInfo = remoteBonusService.promotion(100);
        String result = "bonusService:"+ promotionInfo +";\nmy:ok";
        return result;
    }

}
