package xyz.ersut.service.account.controller;

import com.alibaba.csp.sentinel.context.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bonus")
@Slf4j
public class BonusController {

    @PostMapping("/promotion")
    public String promotion(int amount){
        log.info("promotion org:{}",ContextUtil.getContext().getOrigin());
        return "promotion ok";
    }

}
