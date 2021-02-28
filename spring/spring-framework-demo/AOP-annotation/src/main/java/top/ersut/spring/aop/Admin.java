package top.ersut.spring.aop;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Admin {

    public Admin(){
        this.privateMethod();
    }

    public boolean login(String user){
        System.out.println("登录校验中...");
        //制造异常
//        int i = 10/0;
        if(Objects.equals(user,"wang")){
            return true;
        }
        return false;
    }

    //由于spring aop 是通过动态代理实现的切面，所以私有方法不能代理
    private void privateMethod(){
        System.out.println("私有方法");
    }

    //由于spring aop 是通过动态代理实现的切面，所以静态方法不能代理
    public static void staticMethod(){
        System.out.println("静态方法");
    }

}
