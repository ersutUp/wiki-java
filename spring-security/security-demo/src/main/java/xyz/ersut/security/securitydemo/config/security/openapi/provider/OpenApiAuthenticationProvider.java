package xyz.ersut.security.securitydemo.config.security.openapi.provider;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.SecurityDemoApplication;
import xyz.ersut.security.securitydemo.config.security.openapi.OpenAPIUser;
import xyz.ersut.security.securitydemo.config.security.openapi.filter.OpenAPIFilter;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIAuthenticationToken;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIPrincipal;
import xyz.ersut.security.securitydemo.pojo.entity.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class OpenApiAuthenticationProvider implements AuthenticationProvider {

    private final Map<String,Application> APP = new HashMap<String, Application>(){
        {
            put("123456", Application.builder().appId("123456").appKey("654321").name("test").build());
        }
    };

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(OpenAPIAuthenticationToken.class, authentication,"Only OpenAPIAuthenticationToken is supported");

        OpenAPIPrincipal openAPIPrincipal = (OpenAPIPrincipal) authentication.getPrincipal();

        String appId = openAPIPrincipal.getAppId();
        //数据库获取appkey
        Application application = APP.get(appId);

        //验证sign
        String requestSign = (String) authentication.getCredentials();
        String generateSign = generateSign(openAPIPrincipal.getParams(), appId, application.getAppKey(), openAPIPrincipal.getTimestamp());
        if (log.isDebugEnabled()){
            log.debug("后台生成的签名为：[{}]",generateSign);
        }
        if(!requestSign.equalsIgnoreCase(generateSign)){
            log.info("签名校验失败，请求中的签名为：[{}]，生成的签名为：[{}]",requestSign,generateSign);
            //todo 验证失败抛出认证失败异常
        }

        //组建 OpenAPIUser
        OpenAPIUser openAPIUser = OpenAPIUser.builder().application(application).openAPIPrincipal(openAPIPrincipal).build();

        //获取权限
//        openAPIUser.setPermissions();

        return new OpenAPIAuthenticationToken(openAPIUser,(String) authentication.getCredentials(),openAPIUser.getGrantedAuthorityList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OpenAPIAuthenticationToken.class.isAssignableFrom(authentication));
    }

    //生成签名
    private String generateSign(String params,String appId,String appKey,String timestamp){
        StringBuilder paramAll = new StringBuilder(params);
        //拼接appId
        paramAll.append(OpenAPIFilter.HEADER_APPID)
                .append("=")
                .append(appId)
                .append("&");
        //appKey
        paramAll.append("_appKey")
                .append("=")
                .append(appKey)
                .append("&");
        //时间戳
        paramAll.append(OpenAPIFilter.HEADER_TIMESTAMP)
                .append("=")
                .append(timestamp);

        return DigestUtil.md5Hex(paramAll.toString());
    }
}
