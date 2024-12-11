package xyz.ersut.service.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.ersut.common.constant.ServiceConstants;
import xyz.ersut.service.account.client.fallback.RemoteAccountFallbackFactory;

@FeignClient(
        value = ServiceConstants.SERVICE_ACCOUNT,
        contextId = "remoteBonusService",
        fallbackFactory = RemoteAccountFallbackFactory.class,
        path = "/bonus"
)
public interface RemoteBonusService {

    @PostMapping("/promotion")
    String promotion(@RequestParam int amount);

}
