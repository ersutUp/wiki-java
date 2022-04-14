# spring security 集成api加密

**接口前缀：/api**

### 接口加密逻辑

1. 接口提供方提供 appId 和 appKey
2. 必要参数（通过header传输）
   1. \_appId：调用方Id(上面提到的appId)，接口提供方通过这个参数识别调用方
   2. \_sign：调用接口的签名，根据调用的参数计算得出，服务端通过这个参数防止伪装请求/防止篡改
   3. \_timestamp：13位时间戳，通过时间戳验证请求的有效性，此处使用北京时间
3. GET请求
   1. 将所有请求参数按key做的升序排列,value无需编码。（假设当前时间的时间戳是12345678）例如：有c=3,b=2,a=1 三个参，另加上时间戳和_appId后， 按key排序后为：a=1，b=2，c=3，\_appId=xxx，\_timestamp=12345678
      1. **注意：如果有多选框的值需要过滤出去，例如：提交的参数为 c=3,b=2,b=4,a=1 ，那么b需要过滤出去，参与的值只有  c=3,a=1**

   2. 把参数名和参数值连接成字符串，得到拼装字符：a=1&b=2&c=3&\_appId=xxx&_timestamp=12345678
   3. 用申请到的appKey 连接到接拼装字符串尾部，然后进行32位MD5加密
      1. 示例：假设appKey=test，md5(a=1&b=2&c=3&\_appId=xxx&_timestamp=12345678&\_appKey=test)，取得MD5值36af22a258478500b74ed4d2af8685ae
   4. 最终发送的请求
      1. 参数：a=1，b=2，c=3
      2. header：\_appId=xxx，\_timestamp=12345678，\_sign=36af22a258478500b74ed4d2af8685ae

4. POST请求(json数据)
   1. 在将json串后拼接时间戳、\_appId以及appKey。假设当前时间的时间戳是12345678 ，appId是xxx , appKey是test，json为{"b":1,"a":2}。最终拼接完后是{"b":1,"a":2}\_appId=xxx&_timestamp=12345678&\_appKey=test
   2. 计算签名，MD5进行加密 md5({"b":1,"a":2}&\_appId=xxx&_timestamp=12345678&\_appKey=test) ,那么签名就是a5352e2105915492013e2b93484c218a
   3. 最终发送的请求
      1. json数据：{"b":1,"a":2}
      2. header：\_appId=xxx，\_timestamp=12345678，\_sign=a5352e2105915492013e2b93484c218a


### 服务端（接口提供方）验证请求

1. 获取header中的时间戳，与服务端现在的时间戳比较上下未超出 30秒为合法请求
1. 获取header中的\_appId，在数据库中查询对应的appKey
2. 按照 接口加密逻辑 中的3、4步 计算签名
4. 获取header中的\_sign，将计算出的签名与请求中签名对比，一致则合法请求

### 在spring security中集成

添加认证token，集成AbstractAuthenticationToken抽象类，仿照 UsernamePasswordAuthenticationToken

添加认证拦截器 AbstractAuthenticationProcessingFilter（OpenApiAuthenticationFilter）即集成这个抽象类，仿照UsernamePasswordAuthenticationFilter类，主要用于封装  签名、拼接的字符串、appId 也就是 服务端（接口提供方）验证请求 中的 第2步

添加认证器 AuthenticationProvider（OpenApiAuthenticationProvider） 即实现这个接口，主要用与签名计算与验证也就是 服务端（接口提供方）验证请求 中的 3 4 步

最后配置认证拦截器和认证器

```java
http.authenticationProvider(openApiAuthenticationProvider).addFilterBefore(openApiAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

[配置参考](https://blog.csdn.net/qq_38941937/article/details/97303649)