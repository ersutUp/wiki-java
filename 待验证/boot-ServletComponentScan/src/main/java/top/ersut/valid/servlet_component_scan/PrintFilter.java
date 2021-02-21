package top.ersut.valid.servlet_component_scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/")
public class PrintFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("{} init",this.getClass().getName());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("{} doFilter",this.getClass().getName());
    }

    @Override
    public void destroy() {
        logger.info("{} destroy",this.getClass().getName());
    }
}
