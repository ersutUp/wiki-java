# sentinel源码



## <div id="RequestOriginParser"></div>RequestOriginParser接口的作用

```java
public interface RequestOriginParser {
    String parseOrigin(HttpServletRequest request);
}
```

这个接口用于 给`Context.origin`（调用方身份）赋值，实现了这个接口**限流中的`limitApp`参数才会生效**。



### 💡如何赋值的？

springboot中在`CommonFilter`过滤器中 通过`WebCallbackManager`获取`RequestOriginParser`的实现类并调用`parseOrigin`方法得到`origin`（调用方身份）

**CommonFilter源码**

```java
package com.alibaba.csp.sentinel.adapter.servlet;

public class CommonFilter implements Filter {

    ...
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
      
        ...
        
        String origin = parseOrigin(sRequest);               //1处
        //webContextUnify是控制是否统一web上下文名称
        //WebServletConfig.WEB_SERVLET_CONTEXT_NAME 是统一上下文名称
        //target 一般为URL
        String contextName = webContextUnify ? WebServletConfig.WEB_SERVLET_CONTEXT_NAME : target;
        
        //创建上下文
        ContextUtil.enter(contextName, origin);               //4处
      
        ...
    }

    private String parseOrigin(HttpServletRequest request) {
        
        //重点：获取RequestOriginParser接口的实现类 
        RequestOriginParser originParser = WebCallbackManager.getRequestOriginParser();  //2处
        //EMPTY_ORIGIN是一个空串
        String origin = EMPTY_ORIGIN;
        //如果有实现类 那么使用提供的值，否则使用EMPTY_ORIGIN
        if (originParser != null) {
            
            origin = originParser.parseOrigin(request);            // 3处
            if (StringUtil.isEmpty(origin)) {
                return EMPTY_ORIGIN;
            }
        }
        return origin;
    }  
  
    ...
}
```

- 1处 调用自己类的`parseOrigin`方法
- 2处 从WebCallbackManager获取`RequestOriginParser`的实现类
- 3处 调用实现类的`parseOrigin`方法，获取 origin 值
- 4处 创建上下文 并使用 `RequestOriginParser.parseOrigin`方法的返回值作为**调用方身份**（参数2）



**WebCallbackManager源码**

```java
package com.alibaba.csp.sentinel.adapter.servlet.callback;

public class WebCallbackManager {

    ...
  
    private static volatile RequestOriginParser requestOriginParser = null;

    public static RequestOriginParser getRequestOriginParser() {
        return requestOriginParser;
    }

    public static void setRequestOriginParser(RequestOriginParser requestOriginParser) {
        WebCallbackManager.requestOriginParser = requestOriginParser;
    }
  
    ...
     
}

```

通过静态参数 `requestOriginParser` 存储 `RequestOriginParser` 的实现类



### spring配置示例

通过InitializingBean接口初始化

```java
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
            //重点
            WebCallbackManager.setRequestOriginParser((request1)->{
                String origin = request1.getHeader(SENTINEL_ORIGIN);
                return StringUtil.isNotBlank(origin) ? origin : "";
            });

        }
    }

}

```

