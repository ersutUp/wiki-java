package top.ersut.aspectj;

import java.util.Objects;

public class Admin {

    public Admin(){
        this.privateMethod();
    }

    //final方法可以代理
    public final boolean login(String user){
        System.out.println("登录校验中...");
        if(Objects.equals(user,"wang")){
            return true;
        }
        return false;
    }

    //私有方法可以代理
    private void privateMethod(){
        System.out.println("私有方法");
    }

    //静态方法可以代理
    public static void staticMethod(){
        System.out.println("静态方法");
    }

}
