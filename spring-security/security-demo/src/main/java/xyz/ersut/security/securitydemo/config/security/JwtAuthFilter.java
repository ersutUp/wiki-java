package xyz.ersut.security.securitydemo.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.ersut.security.securitydemo.exception.code.CodeException;
import xyz.ersut.security.securitydemo.exception.login.LoginException;
import xyz.ersut.security.securitydemo.utils.JwtUtil;
import xyz.ersut.security.securitydemo.utils.WebUtils;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private Cache<Long, LoginUser> loginUserCache;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("token");
        //没有token放行
        if(ObjectUtils.isEmpty(token)){
            filterChain.doFilter(request,response);
            return;
        }

        Claims claims;
        //解析token即jwt
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e){
            if (e instanceof ExpiredJwtException) {
                claims = ((ExpiredJwtException) e).getClaims();
                log.info("于[{}]登录过期,token解密信息[{}]",claims.getExpiration(),claims.getSubject());
            }
            WebUtils.renderString(response,objectMapper.writeValueAsString(new ResultJson(ResultSystemCode.AUTH_ERROR,"token不合法")));
            return;
        }
        //获取用户id
        String userId = claims.getSubject();

        //从缓存中获取LoginUser
        LoginUser loginUser = loginUserCache.getIfPresent(new Long(userId));

        if(ObjectUtils.isEmpty(loginUser)){
            log.info("缓存中没有信息,token解密信息[{}]",claims.getSubject());
            WebUtils.renderString(response,objectMapper.writeValueAsString(new ResultJson(ResultSystemCode.AUTH_ERROR,"用户未登录")));
            return;
        }

        //封装 LoginUser 放入 SecurityContextHolder
        Authentication usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser,null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request,response);

    }
}
