package top.ersut.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvocationHandler implements InvocationHandler {

    private Object obj;

    public MyInvocationHandler(Object obj){
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":before");
        //调用被代理类的方法
        Object result = method.invoke(obj,args);
        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":after");
        return result;
    }
}
