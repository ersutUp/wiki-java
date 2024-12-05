package xyz.ersut.service.account.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import xyz.ersut.service.account.client.RemoteAccountService;

@Component
public class RemoteAccountFallbackFactory implements FallbackFactory<RemoteAccountService> {

    @Override
    public RemoteAccountService create(Throwable cause) {
        return new RemoteAccountService() {
            @Override
            public String pay(int amount) {
                return "error fallback";
            }
        };
    }
}
