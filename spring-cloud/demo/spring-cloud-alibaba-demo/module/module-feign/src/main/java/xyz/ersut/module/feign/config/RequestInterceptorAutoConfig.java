package xyz.ersut.module.feign.config;

import feign.Feign;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import xyz.ersut.common.constant.RequestConstants;

@AutoConfiguration
public class RequestInterceptorAutoConfig {


    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private Feign.Builder feignSentinelBuilder;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public RequestInterceptor RequestInterceptorConfig(){
        //拦截器中添加header头
        RequestInterceptor requestInterceptor = requestTemplate -> {
            requestTemplate.header(RequestConstants.REQUEST_SERVER,applicationName);
        };

        //统一添加到feign的客户端中，这里只适配的sentinel
        feignSentinelBuilder.requestInterceptor(requestInterceptor);

        return requestInterceptor;
    }
}
