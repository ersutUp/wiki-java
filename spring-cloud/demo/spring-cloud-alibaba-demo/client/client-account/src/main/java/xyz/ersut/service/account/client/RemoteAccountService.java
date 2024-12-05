package xyz.ersut.service.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.ersut.service.account.client.fallback.RemoteAccountFallbackFactory;

@FeignClient(contextId = "RemoteAccountService",value = "service-account",fallbackFactory = RemoteAccountFallbackFactory.class)
public interface RemoteAccountService {

    @PutMapping("/account/pay")
    String pay(@RequestParam int amount);

}
