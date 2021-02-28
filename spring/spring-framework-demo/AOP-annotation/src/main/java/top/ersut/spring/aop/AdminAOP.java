package top.ersut.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@Aspect
public class AdminAOP{

    @Pointcut("execution(* top.ersut.spring.aop.Admin.login(..))")
    public void loginPointcut(){}

    @Before("execution(* top.ersut.spring.aop.Admin.*(..))")
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

    //spring AOP 的环绕通知与最终通知、返回通知、异常通知不冲突
    @Around("loginPointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("前置环绕通知："+proceedingJoinPoint.getSignature().getName());
        Object obj = proceedingJoinPoint.proceed();
        System.out.println("后置环绕通知："+proceedingJoinPoint.getSignature().getName());
        return obj;
    }

}
