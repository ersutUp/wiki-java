package top.ersut.spring;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyMethodInterceptor implements MethodInterceptor {

    /**
     * @param obj 代理类
     * @param method 被代理类的方法
     * @param args 方法的参数
     * @param proxy 代理类调用父类的方法（super.xxx()）
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":before");
        //调用被代理类的方法,注意是invokeSuper
        Object result = proxy.invokeSuper(obj,args);
        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":after");
        return result;
    }
}
