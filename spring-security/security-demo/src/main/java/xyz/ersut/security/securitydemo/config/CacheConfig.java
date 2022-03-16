package xyz.ersut.security.securitydemo.config;

import com.github.benmanes.caffeine.cache.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.ersut.security.securitydemo.utils.JwtUtil;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public Cache<Long, SecurityConfig.LoginUser> loginUserCache() {
        return Caffeine.newBuilder()
                .initialCapacity(3)
                .expireAfterWrite(JwtUtil.JWT_TTL, TimeUnit.MILLISECONDS)
                .build();
    }

}
