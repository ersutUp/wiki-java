package xyz.ersut.security.securitydemo.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import xyz.ersut.security.securitydemo.config.security.jwt.LoginUser;
import xyz.ersut.security.securitydemo.config.security.openapi.OpenAPIUser;
import xyz.ersut.security.securitydemo.utils.WebUtils;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 授权失败处理器
 */
@Component
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Object user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            Object principal = authentication.getPrincipal();
            if(principal instanceof LoginUser){
                LoginUser loginUser = (LoginUser)principal;
                user = "loginUser-"+loginUser.getUser().getId();
            } else if(principal instanceof OpenAPIUser){
                OpenAPIUser openAPIUser = (OpenAPIUser)principal;
                user = "openAPIUser-"+openAPIUser.getApplication().getAppId();
            } else {
                user = principal;
            }
        }
        log.info("security 认证失败,errMsg[{}],url：[{}],userId:[{}]",authException.getMessage(),request.getRequestURI(),user);
        WebUtils.renderString(response, HttpStatus.UNAUTHORIZED.value(),objectMapper.writeValueAsString(ResultJson.generateResultJson(ResultSystemCode.AUTH_ERROR)));
    }
}
