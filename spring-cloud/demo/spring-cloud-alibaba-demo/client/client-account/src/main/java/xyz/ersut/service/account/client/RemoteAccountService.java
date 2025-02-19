package xyz.ersut.service.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.ersut.common.constant.ServiceConstants;
import xyz.ersut.module.feign.config.RequestInterceptorAutoConfig;
import xyz.ersut.service.account.client.fallback.RemoteAccountFallbackFactory;

@FeignClient(
        value = ServiceConstants.SERVICE_ACCOUNT,
        contextId = "remoteAccountService",
        fallbackFactory = RemoteAccountFallbackFactory.class
)
public interface RemoteAccountService {

    @PutMapping("/account/pay")
    String pay(@RequestParam int amount);

}
