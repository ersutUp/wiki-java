package xyz.ersut.security.securitydemo.config.security.openapi.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIAuthenticationToken;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class OpenAPIFilter extends OncePerRequestFilter {

    public static final String OPEN_API_PREFIX = "/api";

    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        //是否是匹配链接
        if(uri.indexOf(OPEN_API_PREFIX) != 0 ){
            filterChain.doFilter(request,response);
            return;
        }

        //校验时间戳

        //获取参数

        //获取签名

        //排序参数并拼装字符串

        //封装信息
        OpenAPIPrincipal openAPIPrincipal = OpenAPIPrincipal
                                                    .builder()
                                                    .appid("111")
                                                    .params("a=1&b=2&c=3&_appid=xxx&_timestamp=12345678&appkey=test")
                                                    .build();

        //封装 LoginUser 放入 SecurityContextHolder
        Authentication openAPIAuthenticationToken = new OpenAPIAuthenticationToken(openAPIPrincipal,"36af22a258478500b74ed4d2af8685ae");

        //进行认证
        Authentication authenticate = authenticationManager.authenticate(openAPIAuthenticationToken);

        //判断认证是否成功
        if(authenticate != null){
            //放入 SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }


        filterChain.doFilter(request,response);

    }
}
