package xyz.ersut.security.securitydemo.config.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.ersut.security.securitydemo.config.security.jwt.LoginUser;
import xyz.ersut.security.securitydemo.config.security.jwt.pojo.JwtInfo;
import xyz.ersut.security.securitydemo.config.security.jwt.token.JwtToken;
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
            throw new CredentialsExpiredException("登录失效");
        }
        //获取用户id
        String json = claims.getSubject();

        JwtInfo jwtInfo = objectMapper.readValue(json,JwtInfo.class);

        //封装 LoginUser 放入 SecurityContextHolder
        JwtToken jwtToken = new JwtToken(token, jwtInfo.getPermissions());
        SecurityContextHolder.getContext().setAuthentication(jwtToken);

        filterChain.doFilter(request,response);
    }
}
