package top.ersut.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//调整切面的顺序 值越大越接近程序
@Order(1)
@Component
@Aspect
public class AdminAOP2 {

    @Before("execution(* top.ersut.spring.aop.Admin.*(..))")
    public void before(JoinPoint joinPoint){
        System.out.println("前置通知：[AdminAOP2] "+joinPoint.getSignature().getName());
    }


    @After("top.ersut.spring.aop.AdminAOP.loginPointcut()")
    public void after(JoinPoint joinPoint){
        System.out.println("最终通知：[AdminAOP2] "+joinPoint.getSignature().getName());
    }

}
