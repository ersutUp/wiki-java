package com.alibaba.csp.sentinel.demo.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {


    /**
     * 支持limitApp参数
     */
    @Bean
    public RequestOriginParserInit requestOriginParserInit(){
        return new RequestOriginParserInit();
    }


    public static class RequestOriginParserInit implements InitializingBean{
        private static final String SENTINEL_ORIGIN = "sentinel-origin";

        @Override
        public void afterPropertiesSet() throws Exception {
            WebCallbackManager.setRequestOriginParser((request1)->{
                String origin = request1.getHeader(SENTINEL_ORIGIN);
                return StringUtil.isNotBlank(origin) ? origin : "";
            });
        }
    }

}
