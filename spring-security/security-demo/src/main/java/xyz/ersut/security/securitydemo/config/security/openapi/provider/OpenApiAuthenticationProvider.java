package xyz.ersut.security.securitydemo.config.security.openapi.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import xyz.ersut.security.securitydemo.SecurityDemoApplication;
import xyz.ersut.security.securitydemo.config.security.openapi.OpenAPIUser;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIAuthenticationToken;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIPrincipal;
import xyz.ersut.security.securitydemo.pojo.entity.Application;

public class OpenApiAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(OpenAPIAuthenticationToken.class, authentication,"Only OpenAPIAuthenticationToken is supported");

        OpenAPIPrincipal openAPIPrincipal = (OpenAPIPrincipal) authentication.getPrincipal();

        //数据库获取appkey

        Application application = Application.builder().appid("111").appkey("test").name("测试").build();

        //验证sign

            //验证失败抛出认证失败异常

        //获取权限

        //组建 OpenAPIUser
        OpenAPIUser openAPIUser = OpenAPIUser.builder().application(application).openAPIPrincipal(openAPIPrincipal).build();

        return new OpenAPIAuthenticationToken(openAPIUser,(String) authentication.getCredentials(),openAPIUser.getGrantedAuthorityList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OpenAPIAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
