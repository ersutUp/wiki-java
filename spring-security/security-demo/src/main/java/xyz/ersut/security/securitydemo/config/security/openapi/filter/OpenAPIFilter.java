package xyz.ersut.security.securitydemo.config.security.openapi.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class OpenAPIFilter extends OncePerRequestFilter {

    public static final String OPEN_API_PREFIX = "/api";

    //header头的key
    private static final String HEADER_APPID = "_appId";
    private static final String HEADER_SIGN = "_sign";
    private static final String HEADER_TIMESTAMP = "_timestamp";

    //超时时间
    private static final int OVERTIME = 30000;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

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
        if (!timestampVerify(response,timestamp)) {
            log.info("时间戳出错,url:[{}]，请求中的时间戳：[{}]",request.getRequestURI(),timestamp);
            return;
        }

        //获取签名

        //获取参数


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
