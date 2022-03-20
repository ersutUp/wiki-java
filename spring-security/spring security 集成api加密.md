# spring security 集成api加密

**接口前缀：/openapi**

### 接口加密逻辑

1. 接口提供方提供 appId 和 appkey
2. 必要参数
   1. \_appId：调用方id(上面提到的appId)，接口提供方通过这个参数识别调用方
   2. \_sign：调用接口的签名，根据调用的参数计算得出，服务端通过这个参数防止伪装请求/防止篡改
   3. \_timestamp：13位时间戳，通过时间戳验证请求的有效性
3. 对除签名外的所有请求参数按key做的升序排列,value无需编码。（假设当前时间的时间戳是12345678）例如：有c=3,b=2,a=1 三个参，另加上时间戳后， 按key排序后为：a=1，b=2，c=3，\_appid=xxx，\_timestamp=12345678
4. 把参数名和参数值连接成字符串，得到拼装字符：a=1&b=2&c=3&\_appid=xxx&_timestamp=12345678
5. 用申请到的appkey 连接到接拼装字符串尾部，然后进行32位MD5加密
   1. 示例：假设appkey=test，md5(a=1&b=2&c=3&\_appid=xxx&_timestamp=12345678&appkey=test)，取得MD5摘要值36af22a258478500b74ed4d2af8685ae
6. 最终发送请求的参数：a=1，b=2，c=3，\_appid=xxx，\_timestamp=12345678，\_sign=36af22a258478500b74ed4d2af8685ae

### 服务端（接口提供方）验证请求

1. 获取参数中的时间戳，与服务端现在的时间戳比较上下未超出 30秒为合法请求
2. 获取所有参数去除_sign参数后将其余参数按照 接口加密逻辑 中的3、4步 拼装字符串
3. 获取参数中的appid，在数据库中查询对应的appkey
4. 按照 接口加密逻辑 中的第5步计算签名，将计算出的签名与请求中签名对比，一致则合法请求

### 在spring security中集成

添加认证token，集成AbstractAuthenticationToken抽象类，仿照 UsernamePasswordAuthenticationToken

添加认证拦截器 AbstractAuthenticationProcessingFilter（OpenApiAuthenticationFilter）即集成这个抽象类，仿照UsernamePasswordAuthenticationFilter类，主要用于封装  签名、拼接的字符串、appid 也就是 服务端（接口提供方）验证请求 中的 第2步

添加认证器 AuthenticationProvider（OpenApiAuthenticationProvider） 即实现这个接口，主要用与签名计算与验证也就是 服务端（接口提供方）验证请求 中的 3 4 步

最后配置认证拦截器和认证器

```java
http.authenticationProvider(openApiAuthenticationProvider).addFilterBefore(openApiAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

[配置参考](https://blog.csdn.net/qq_38941937/article/details/97303649)