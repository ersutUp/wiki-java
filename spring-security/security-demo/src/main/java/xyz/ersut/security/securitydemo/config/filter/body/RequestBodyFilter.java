package xyz.ersut.security.securitydemo.config.filter.body;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import xyz.ersut.security.securitydemo.config.filter.body.request.RequestBodyWrapper;
import xyz.ersut.security.securitydemo.config.filter.body.response.ResponseBodyWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Configuration
@WebFilter(value = "/*",filterName = "bodyFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestBodyFilter implements Filter {

    public RequestBodyFilter() {
        log.info("RequestBodyFilter 生效");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        RequestBodyWrapper myRequestWrapper = new RequestBodyWrapper((HttpServletRequest) servletRequest);
        ResponseBodyWrapper myResponseBodyWrapper = new ResponseBodyWrapper((HttpServletResponse)servletResponse);

        filterChain.doFilter(myRequestWrapper, myResponseBodyWrapper);

        //获取响应数据
        byte[] content = myResponseBodyWrapper.getResponseData();
        //这三行是必须的！！！！获取原本的ResponseBodyWrapper包装的 response ，将响应数据放入
        OutputStream out = servletResponse.getOutputStream();
        out.write(content);
        out.flush();
    }

    @Override
    public void destroy() {

    }
}
