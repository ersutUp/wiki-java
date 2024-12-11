# Sentinel流量治理框架

> 介绍：
>
> 随着微服务的流行，服务和服务之间的稳定性变得越来越重要。Sentinel 是面向分布式、多语言异构化服务架构的流量治理组件，主要**以流量为切入点，从流量路由、流量控制、流量整形、熔断降级、系统自适应过载保护、热点流量防护等多个维度来帮助开发者保障微服务的稳定性**。



## 持久化Nacos

[Spring Boot 示例](./demo/spring-sentinel-demo/src/main/java/com/alibaba/csp/sentinel/demo/config/NacosConfiguration.java)



Spring Cloud示例，通过配置文件即可

```yaml
# Spring
spring: 
  cloud:
    sentinel:
      # nacos配置持久化
      datasource:
        ds1:
          nacos:
            server-addr: 127.0.0.1:8848
            dataId: sentinel-gateway-flow
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: gw-flow
        ds2:
          nacos:
            server-addr: 127.0.0.1:8848
            dataId: sentinel-api-group-gateway
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: gw-api-group

```





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

#### 单资源限流

FlowRule限流对象如下（使用nacos中json表示）

```json
[
    {
        "resource":"query",
        "count":300
    }
]
```

当query资源QPS达到300时进行限流



#### 链路限流（strategy：2）

该模式生效需要将 `webContextUnify` 设置为`false`

> webContextUnify 解释：是否统一web上下文名称

| 值    | 描述                                                         | 从控制台中观察                                     |
| ----- | ------------------------------------------------------------ | -------------------------------------------------- |
| false | 将URL中的Path设置为该链路的根资源名                          | [点击查看图片](./images/webContextUnify-false.png) |
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

##### FlowRule限流对象如下（使用nacos中json表示）

```json
[
    {
        //资源名
        "resource":"floor",
        "count":500,
        //链路限流模式
        "strategy": 2,
        //链路入口
        "ref-resource":"/chain/flow"
    }
]
```

说明：当**资源floor**被**`/chain/flow`请求**时，限流QPS：500

##### 代码demo

- [配置](./demo/spring-sentinel-demo/src/main/java/com/alibaba/csp/sentinel/demo/config/FilterConfig.java)
- 请求：http://127.0.0.1:19966/chain/flow



#### 调用方限流（limitApp）

> 官方文档：https://sentinelguard.io/zh-cn/docs/flow-control.html 其中的 3.1 根据调用方限流

Sentinel上下文中的 `Context.origin` 参数标明了调用方身份，在spring中每个请求只有一个sentinel的上下文，也就是说**不管这个请求中使用了多少个资源，来源都是一致的**



##### 💡limitApp参数如何生效？

> Spring-boot中需要把 CommonFilter 添加到过滤器中

**1、需要实现`RequestOriginParser`接口**

```java
public interface RequestOriginParser {
    String parseOrigin(HttpServletRequest request);
}
```

通过`parseOrigin`方法返回的值会赋值给`Context.origin`

**2、通过InitializingBean接口把实现类注册到WebCallbackManager中**

这里是以Spring Boot为例

```java
package com.alibaba.csp.sentinel.demo.config;

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

```

⭐️**为什么要注册到WebCallbackManager中**，[看源码](./sentinel源码.md#RequestOriginParser)



##### FlowRule对象如下（使用nacos中json表示）

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



##### 代码demo

- [配置](./demo/spring-sentinel-demo/src/main/java/com/alibaba/csp/sentinel/demo/config/InitConfig.java)
- 请求：http://127.0.0.1:19966/limitApp ， header头的key：sentinel-origin



#### 关联限流（strategy：1）

官方说明：

当两个资源之间具有资源争抢或者依赖关系的时候，这两个资源便具有了关联。

比如对数据库同一个字段的读操作和写操作存在争抢，读的速度过高会影响写得速度，写的速度过高会影响读的速度。如果放任读写操作争抢资源，则争抢本身带来的开销会降低整体的吞吐量。可使用关联限流来避免具有关联关系的资源之间过度的争抢，举例来说，`read_db` 和 `write_db` 这两个资源分别代表数据库读写，我们可以给 `read_db` 设置限流规则来达到写优先的目的：设置 `FlowRule.strategy` 为 `RuleConstant.RELATE` 同时设置 `FlowRule.ref_identity` 为 `write_db`。这样当写库操作过于频繁时，读数据的请求会被限流。



这个配置的FlowRule对象如下：（使用Nacos中的json表示）

```json
[
    {
        //资源名
        "resource":"read_db",
        "count":500,
        //链路限流模式
        "strategy": 1,
        //链路入口
        "ref-resource":"write_db"
    }
]
```

当资源`write_db`的QPS达到500时，资源`read_db`被限流。



##### 其他例子，两个关联的链接限流

**创建表单链接**和**获取表单信息链接**，当创建表单的QPS达到300时，对获取表单进行限流。

```java
package com.alibaba.csp.sentinel.demo.controller;

@RestController
public class FlowDemoController {
    //获取表单信息
    @GetMapping("/form/get")
    public String formGet() {
        return "ok";
    }

    //创建表单
    @GetMapping("/form/add")
    public String formAdd() {
        return "ok";
    }
}
```



**FlowRule限流对象如下（使用nacos中json表示）**

```json
[ 
    {
        "resource":"/form/get",
        "count":300,
        "strategy": 1,
        "ref-resource":"/form/add"
    }
]
```

说明：当**创建表单的QPS达到300**时，获取表单信息被限流



### 熔断降级规则(DegradeRule)



spring cloud Gateway 集成 sentinel 不支持熔断，一般来说**使用在服务与服务之间的调用，或者服务与第三方服务直接的调用。**

例如：集成在`Feign`中，请求异常时进行熔断降级。[示例代码](./demo/spring-cloud-alibaba-demo/client/client-account/src/main/java/xyz/ersut/service/account/client/RemoteAccountService.java)



### 系统保护规则(SystemRule)

——

### 黑白名单规则/来源访问控制(AuthorityRule)

通过来源（`ContextUtil.enter(contextName, origin);`,参数2就是来源）控制资源是否可以访问。



#### 有两个模式：

- 白名单

  - 示例：

    ```json
      {
        "resource": "/order",
        "limitApp": "h5,pc",
        "strategy": 0
      }
    ```

    只允许来源是h5和pc

- 黑名单

  - 示例：

    ```json
      {
        "resource": "/order1",
        "limitApp": "mobile",
        "strategy": 1
      }
    ```

    拒绝来源是mobile，其他都允许

💡注意：

- 如果**来源是空值，是允许访问**，不受黑白名单的控制。
- **一个资源只允许一个模式**，要么黑名单模式要么白名单模式。



#### 自定义来源：

Spring Cloud中通过实现 RequestOriginParser 接口，并注入到bean中生效。

```java
    @Bean
    public RequestOriginParser RequestOriginParser(){
        return (request) -> {
            //从请求头中获取来源
            String header = request.getHeader(RequestConstants.REQUEST_SERVER);
            if(Objects.isNull(header) || header.isBlank()){
                //这里是为了避免空串，不受黑白名单控制，所以返回字符串"null"
                return "null";
            }
            return header;
        };
    }
```

[示例代码](./demo/spring-cloud-alibaba-demo/module/module-sentinel/src/main/java/xyz/ersut/module/sentinel/config/SentinelAutoConfig.java)



#### 适用场景

- 通过黑名单控制要拒绝的IP
  - 但是IP黑名单一般在防火墙做，到不了应用层，除非对IP黑名单有复杂需求，即使有复杂要求也是在过滤器实现。
- 控制服务的访问
  - [示例项目](./demo/spring-cloud-alibaba-demo)
  - 具体代码部分查看标签[sentinel-origin-service-name](https://github.com/ersutUp/wiki-java/releases/tag/sentinel-origin-service-name)的提交
  - 主要类：
    - ForwardHeaderFilter    网关转发时将项目名添加header头
    - SentinelAutoConfig      解析请求头，给sentinel设置来源
    - RequestInterceptorAutoConfig   fegin请求时添加项目名请求头

其他场景暂时未想到。





### 热点参数规则(ParamFlowRule)

