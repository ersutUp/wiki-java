# Sentinel流量治理框架

> 介绍：
>
> 随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Sentinel 是面向分布式、多语言异构化服务架构的流量治理组件，主要**以流量为切入点，从流量路由、流量控制、流量整形、熔断降级、系统自适应过载保护、热点流量防护等多个维度来帮助开发者保障微服务的稳定性**。



## 规则的类型

### 流量控制规则(FlowRule)

**两种限流阈值类型：**

1. **QPS模式**
2. **线程数**

#### 重要属性：

|      Field      | 说明                                                         | 默认值                        |
| :-------------: | :----------------------------------------------------------- | :---------------------------- |
|    resource     | 资源名，资源名是限流规则的作用对象                           |                               |
|      count      | 限流阈值                                                     |                               |
|      grade      | 限流阈值类型，QPS 或线程数模式。 0 代表根据并发数量来限流，1 代表根据 QPS 来进行流量控制。 | QPS 模式                      |
|    limitApp     | 流控针对的调用来源                                           | `default`，代表不区分调用来源 |
|    strategy     | 调用关系限流策略：0：直接、1：关联、2：链路                  | 根据资源本身（直接）          |
| controlBehavior | 流控效果（0：直接拒绝 /1： 慢启动模式 / 2：排队等待），不支持按调用关系限流 | 直接拒绝                      |
|   refResource   | 相关的资源，strategy为关联模式或链路模式时使用               | ""                            |

#### 



#### 链路限流（strategy：2）

该模式生效需要将 `webContextUnify` 设置为`false`

> webContextUnify 解释：是否统一web上下文名称

| 值    | 描述                                                         | 从控制台中观察                                     |
| ----- | ------------------------------------------------------------ | -------------------------------------------------- |
| false | 将url设置为该链路的根资源名                                  | [点击查看图片](./images/webContextUnify-false.png) |
| true  | 根资源为 sentinel_web_servlet_context（spring cloud中是sentinel_spring_web_context） | [点击查看图片](./images/webContextUnify-true.png)  |

spring boot中配置：

```java
@Bean
public FilterRegistrationBean sentinelFilterRegistration() {
    FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new CommonFilter());
    registration.addUrlPatterns("/*");
    //这里
    registration.addInitParameter(CommonFilter.WEB_CONTEXT_UNIFY,"false");
    registration.setName("sentinelFilter");
    registration.setOrder(1);

    return registration;
}
```

spring cloud中配置：

```yaml
spring:
  cloud:
    sentinel:
      web-context-unify: false
```

##### nacos中json示例

```json
[
    {
        //资源名
        "resource":"floor",
        "count":500,
        //链路限流模式
        "strategy": 2,
        //链路入口
        "ref-resource":"/chain/test"
    }
]
```

说明：当**资源floor**被**`/chain/test`请求**时，限流QPS：500



#### 调用方限流（limitApp）

> 官方文档：https://sentinelguard.io/zh-cn/docs/flow-control.html 其中的 3.1 根据调用方限流

Sentinel上下文中的 `Context.origin` 参数标明了调用方身份，在spring中每个请求只有一个sentinel的上下文，也就是说**不管这个请求中使用了多少个资源，来源都是一致的**



##### 💡limitApp参数如何生效？

**1、需要实现`RequestOriginParser`接口**

```java
public interface RequestOriginParser {
    String parseOrigin(HttpServletRequest request);
}
```

通过`parseOrigin`方法返回的值会赋值给`Context.origin`

**2、通过Filter把实现类注册到WebCallbackManager中**

这里是以Spring Boot为例

```java

@Configuration
public class SentinelOriginFilterConfig {

    private static final String SENTINEL_ORIGIN = "sentinel-origin";

    @Bean
    public FilterRegistrationBean SentinelOriginFilter(){
        FilterRegistrationBean<Filter> sentinelOriginFilter = new FilterRegistrationBean();

        sentinelOriginFilter.setFilter((servletRequest,servletResponse,filterChain)->{
            WebCallbackManager.setRequestOriginParser((request1)->{
                //获取请求头的数据
                String origin = request1.getHeader(SENTINEL_ORIGIN);
                return StringUtil.isNotBlank(origin) ? origin : "";
            });
            filterChain.doFilter(servletRequest,servletResponse);
        });

        sentinelOriginFilter.addUrlPatterns("/*");
        //需要注意顺序，要在CommonFilter之前
        sentinelOriginFilter.setOrder(0);
        sentinelOriginFilter.setName("sentinelOriginFilter");
        return sentinelOriginFilter;
    }
}
```



##### nacos中json示例

```json
[
    {
        "resource":"/limitApp",
        "count":100,
        "limit-app":"pc"
    }
]
```

limit-app中的值与请求头 sentinel-origin 的值对应。

这个配置的意思是 **当 ` /limitApp`请求中的`sentinel-origin`请求头的值为`pc`**时 限制QPS：100



👇🏻下面这个请求**会触发限流**👇🏻

```shell
curl --location --request GET 'http://127.0.0.1:19966/limitApp' \
--header 'sentinel-origin: pc'
```

👇🏻下面这个请求**不会触发限流**👇🏻

```shell
curl --location --request GET 'http://127.0.0.1:19966/limitApp' \
--header 'sentinel-origin: app'
```

👆🏻上面两个链接不同之处就是请求头`sentinel-origin`的值不同👆🏻





限流过滤



### 熔断降级规则(DegradeRule)



### 系统保护规则(SystemRule)



### 黑白名单规则(AuthorityRule)



### 热点参数规则(ParamFlowRule)

