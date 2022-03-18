package xyz.ersut.security.securitydemo.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import xyz.ersut.security.securitydemo.exception.code.CodeException;
import xyz.ersut.security.securitydemo.exception.login.LoginException;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;
import xyz.ersut.security.securitydemo.utils.result.code.Resultcode;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 全局异常处理
 *
 * @author 王二飞
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerConfig {



    @ExceptionHandler(value = LoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultJson loginExceptionHandler(HttpServletRequest request, LoginException loginException){
        log.info("认证失败,errMsg[{}]",loginException.getMessage());
        return new ResultJson(ResultSystemCode.AUTH_ERROR,loginException.getMessage());
    }




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