package xyz.ersut.security.securitydemo.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
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
 * 认证出错处理器
 */
@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Object user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
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
        log.info("security 权限不足,errMsg[{}],url：[{}],userId:[{}]", accessDeniedException.getMessage(), request.getRequestURI(), user);
        WebUtils.renderString(response, HttpStatus.FORBIDDEN.value(), objectMapper.writeValueAsString(ResultJson.generateResultJson(ResultSystemCode.PERMISSIONS_ERROR)));

    }
}
