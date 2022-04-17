package xyz.ersut.security.securitydemo.config.security.openapi.filter;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIAuthenticationToken;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIPrincipal;
import xyz.ersut.security.securitydemo.utils.WebUtils;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Slf4j
@Component
public class OpenAPIFilter extends OncePerRequestFilter {

    public static final String OPEN_API_PREFIX = "/api";

    //header头的key
    public static final String HEADER_APPID = "_appId";
    public static final String HEADER_SIGN = "_sign";
    public static final String HEADER_TIMESTAMP = "_timestamp";

    //超时时间
    private static final int OVERTIME = 30000;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        //是否是匹配链接
        if(uri.indexOf(OPEN_API_PREFIX) != 0 ){
            filterChain.doFilter(request,response);
            return;
        }

        /**校验时间戳*/
        String timestamp = request.getHeader(HEADER_TIMESTAMP);
        if (log.isDebugEnabled()){
            log.debug("当前请求的时间戳：[{}],url:[{}]",timestamp,uri);
        }
        if (!timestampVerify(response,timestamp)) {
            log.info("时间戳出错,url:[{}]，请求中的时间戳：[{}]",uri,timestamp);
            return;
        }

        //获取签名
        String sign = request.getHeader(HEADER_SIGN);
        if (log.isDebugEnabled()){
            log.debug("当前请求的签名：[{}],url:[{}]",sign,uri);
        }
        if(ObjectUtils.isEmpty(sign)){
            log.info("没有签名,url:[{}]",uri);
            WebUtils.renderString(response, HttpStatus.BAD_REQUEST.value(), objectMapper.writeValueAsString(ResultJson.generateResultJson(ResultSystemCode.ILLEGAL)));
            return;
        }

        //获取appId
        String appId = request.getHeader(HEADER_APPID);
        if (log.isDebugEnabled()){
            log.debug("当前请求的appId：[{}],url:[{}]",appId,uri);
        }
        if(ObjectUtils.isEmpty(appId)){
            log.info("没有appid,url:[{}]",uri);
            WebUtils.renderString(response, HttpStatus.BAD_REQUEST.value(), objectMapper.writeValueAsString(ResultJson.generateResultJson(ResultSystemCode.ILLEGAL)));
            return;
        }

        String method = request.getMethod();
        if (log.isDebugEnabled()){
            log.debug("当前请求的方法：[{}],url:[{}]",method,uri);
        }
        StringBuilder params = new StringBuilder("");
        //get请求获取参数 非get请求获取json串
        if("GET".equalsIgnoreCase(method)){
            SortedMap<String, String[]> parameterMap = new TreeMap<>(request.getParameterMap());
            //判断参数不为空
            if(!ObjectUtils.isEmpty(parameterMap)){
                Iterator<SortedMap.Entry<String, String[]>> ParamIterator = parameterMap.entrySet().iterator();
                //遍历并排序参数
                while (ParamIterator.hasNext()){
                    SortedMap.Entry<String, String[]> entry = ParamIterator.next();
                    String[] value = entry.getValue();
                    //筛选value值为多个的参数
                    if(value.length == 1){
                        params.append(entry.getKey());
                        params.append("=");
                        params.append(value[0]);
                        params.append("&");
                    }
                }
            }
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String body = IoUtil.read(reader);
            if(!ObjectUtils.isEmpty(body)){
                body += "&";
            }
            params.append(body);
        }
        if (log.isDebugEnabled()){
            log.debug("当前请求中的参数：[{}],url:[{}]",params,uri);
        }


        //封装信息
        OpenAPIPrincipal openAPIPrincipal = OpenAPIPrincipal
                                                    .builder()
                                                    .appId(appId)
                                                    .params(params.toString())
                                                    .timestamp(timestamp)
                                                    .build();

        //封装 LoginUser 放入 SecurityContextHolder
        Authentication openAPIAuthenticationToken = new OpenAPIAuthenticationToken(openAPIPrincipal,sign);

        Authentication authenticate = null;
        try{
            //进行认证
            authenticate = authenticationManager.authenticate(openAPIAuthenticationToken);
        } catch (BadCredentialsException e){
            if(log.isDebugEnabled()){
                log.debug("认证失败：["+e.getMessage()+"]",e);
            }
            authenticationEntryPoint.commence(request,response,e);
            return;
        }

        //判断认证是否成功
        if(authenticate != null){
            //放入 SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }

        filterChain.doFilter(request,response);

    }

    /**
     * 时间戳检验
     */
    private boolean timestampVerify(HttpServletResponse response,String timestamp) throws JsonProcessingException {
        //判断是不是空的
        if(ObjectUtils.isEmpty(timestamp)){
            //非法请求 打回
            log.info("没有时间戳");
            WebUtils.renderString(response, HttpStatus.BAD_REQUEST.value(), objectMapper.writeValueAsString(ResultJson.generateResultJson(ResultSystemCode.ILLEGAL)));
            return false;
        }
        Long timestampLong = new Long(timestamp);

        //获取当前时间戳并校验请求中的时间戳(上下30秒)
        long timestampNow = new Date().getTime();
        if((timestampNow - OVERTIME) > timestampLong || timestampLong > (timestampNow + OVERTIME)){
            //访问超时 打回
            log.info("时间戳过期");
            WebUtils.renderString(response, HttpStatus.BAD_REQUEST.value(), objectMapper.writeValueAsString(ResultJson.generateResultJson(ResultSystemCode.TIMEOUT)));
            return false;
        }
        return true;
    }



}
