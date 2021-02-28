package top.ersut.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Objects;

@Aspect
public class AdminAOP{

    @Pointcut("execution(* top.ersut.aspectj.Admin.login(..))")
    public void loginPointcut(){}

    @Before("execution(* top.ersut.aspectj.Admin.*(..))")
    public void before(JoinPoint joinPoint){
        System.out.println("前置通知："+joinPoint.getSignature().getName());
    }

    @After("loginPointcut()")
    public void after(JoinPoint joinPoint){
        System.out.println("最终通知："+joinPoint.getSignature().getName());
    }

    @AfterReturning("loginPointcut()")
    public void afterReturn(JoinPoint joinPoint){
        System.out.println("返回通知："+joinPoint.getSignature().getName());
    }

    @AfterThrowing("loginPointcut()")
    public void afterThrowing(JoinPoint joinPoint){
        System.out.println("异常通知："+joinPoint.getSignature().getName());
    }

//    @Around("execution(* top.ersut.aspectj.Admin.login(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("环绕通知：["+proceedingJoinPoint.getArgs()[0]+"]登录结...");
        Object obj = proceedingJoinPoint.proceed();
        System.out.println("环绕通知：["+proceedingJoinPoint.getArgs()[0]+"]登录束...");
        return obj;
    }

}
