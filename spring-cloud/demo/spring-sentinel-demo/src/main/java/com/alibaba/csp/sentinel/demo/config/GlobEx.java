package com.alibaba.csp.sentinel.demo.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobEx{

    @ExceptionHandler(BlockException.class)
    public void handlerBlockException(BlockException e, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
        response.getWriter().print("err");
    }
}
