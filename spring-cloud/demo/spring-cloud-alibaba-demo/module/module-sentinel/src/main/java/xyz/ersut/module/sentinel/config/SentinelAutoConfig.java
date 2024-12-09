package xyz.ersut.module.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.Objects;

@AutoConfiguration
@Order
public class SentinelAutoConfig {
    public static final String SENTINEL_ORIGIN = "sentinel-origin";

    @ConditionalOnMissingBean
    @Bean
    public RequestOriginParser RequestOriginParser(){
        return (request) -> {
            String header = request.getHeader(SENTINEL_ORIGIN);
            if(Objects.isNull(header) || header.isBlank()){
                return "";
            }
            return header;
        };
    }

}
