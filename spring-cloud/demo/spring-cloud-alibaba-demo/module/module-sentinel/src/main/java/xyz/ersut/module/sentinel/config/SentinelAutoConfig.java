package xyz.ersut.module.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import xyz.ersut.common.constant.RequestConstants;

import java.util.Objects;

@AutoConfiguration
@Order
public class SentinelAutoConfig {

    /**
     * 设置sentinel的来源
     */
    @ConditionalOnMissingBean
    @Bean
    public RequestOriginParser RequestOriginParser(){
        return (request) -> {
            String header = request.getHeader(RequestConstants.REQUEST_SERVER);
            if(Objects.isNull(header) || header.isBlank()){
                return "null";
            }
            return header;
        };
    }

}
