package top.ersut.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public class AdminAOP{


    public void before(JoinPoint joinPoint){
        System.out.println("前置通知："+joinPoint.getSignature().getName());
    }

    public void after(JoinPoint joinPoint){
        System.out.println("最终通知："+joinPoint.getSignature().getName());
    }

    public void afterReturn(JoinPoint joinPoint){
        System.out.println("返回通知："+joinPoint.getSignature().getName());
    }

    public void afterThrowing(JoinPoint joinPoint){
        System.out.println("异常通知："+joinPoint.getSignature().getName());
    }

    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("前置环绕通知："+proceedingJoinPoint.getSignature().getName());
        Object obj = proceedingJoinPoint.proceed();
        System.out.println("后置环绕通知："+proceedingJoinPoint.getSignature().getName());
        return obj;
    }

}
