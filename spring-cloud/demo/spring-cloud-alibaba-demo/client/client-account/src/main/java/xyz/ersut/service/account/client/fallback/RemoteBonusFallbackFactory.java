package xyz.ersut.service.account.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import xyz.ersut.service.account.client.RemoteAccountService;
import xyz.ersut.service.account.client.RemoteBonusService;

@Component
public class RemoteBonusFallbackFactory implements FallbackFactory<RemoteBonusService> {

    @Override
    public RemoteBonusService create(Throwable cause) {
        return new RemoteBonusService() {
            @Override
            public String promotion(int amount) {
                return "err fallback";
            }
        };
    }
}
