package xyz.ersut.security.securitydemo.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import xyz.ersut.security.securitydemo.exception.code.CodeException;

import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;
import xyz.ersut.security.securitydemo.utils.result.code.Resultcode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 全局异常处理
 *
 * @author 王二飞
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerConfig {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    /*spring security*/
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void accessDeniedHandler(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws ServletException, IOException {
        accessDeniedHandler.handle(request,response,accessDeniedException);
    }
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void authenticationExceptionHandler(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws ServletException, IOException {
        authenticationEntryPoint.commence(request,response,authenticationException);
    }


    /** 自定义异常 */
    @ExceptionHandler(value = CodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultJson codeExceptionHandler(HttpServletRequest request, CodeException codeException) {
        //从异常中获取状态码
        Resultcode resultcode = codeException.getResultcode();
        log.info("CodeException:param validated err,errMsg[{}]",codeException.getMessage());
        String showMessage = codeException.getShowMessage();
        if(ObjectUtils.isEmpty(showMessage)){
            return new ResultJson(resultcode);
        } else {
            return new ResultJson(resultcode,showMessage);
        }

    }


    /**
     * 对 @Validated 验证失败的处理
     * body pararm
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultJson methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        String errorMsg = errorMsg(bindingResult);

//        log.field("errInfo",errorMsg).field("errMsg",exception.getMessage()).info("MethodArgumentNotValidException:param validated err");

        return new ResultJson(ResultSystemCode.PARAM_ERROR,errorMsg);
    }

    /**
     * 对 @Validated 验证失败的处理
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = BindException .class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultJson constraintViolationExceptionHandler(HttpServletRequest request, BindException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        String errorMsg = errorMsg(bindingResult);

//        log.field("errInfo",errorMsg).field("errMsg",exception.getMessage()).info("BindException:param validated err");

        return new ResultJson(ResultSystemCode.PARAM_ERROR,errorMsg);
    }


    /**
     * 其他异常处理
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("system error",e);
        return new ResultJson(ResultSystemCode.ERROR);
    }

    /**
     * 获取验证注解的 message
     * @param bindingResult
     * @return
     */
    private String errorMsg(BindingResult bindingResult){
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder builder = new StringBuilder();
        builder.append(fieldErrors.get(0).getDefaultMessage());
        return builder.toString();
    }
}
