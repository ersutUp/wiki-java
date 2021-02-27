package top.ersut.spring;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import top.ersut.spring.server.UserServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


class MyMethodInterceptorTest {

    @Test
    void tset() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //不使用代理
        System.out.println("不使用代理\n");
        UserServer userServer = new UserServer();
        userServer.add();
        System.out.println();
        userServer.all();

        System.out.println("----------------------");

        //使用代理
        System.out.println("使用代理\n");
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(UserServer.class);
//        enhancer.setCallback(new MyMethodInterceptor());
        Object userServerObject = Enhancer.create(UserServer.class,null,new MyMethodInterceptor());

        UserServer userServerProxy = (UserServer)userServerObject;
        System.out.println("父类："+userServerProxy.getClass().getSuperclass().getSimpleName());
        userServerProxy.add();
        System.out.println();
        userServerProxy.all();
        System.out.println();
        //final方法无法代理
        userServerProxy.finalMethod();
        System.out.println();
        //静态方法无法代理
        userServerProxy.staticMethod();
        System.out.println();
        //通过反射获取静态方法依旧是UserServer类的addStatic方法,查看字节码发现没有代理静态方法
        Method addStatic = userServerObject.getClass().getMethod("staticMethod");
        addStatic.invoke(userServerObject,null);

    }
}