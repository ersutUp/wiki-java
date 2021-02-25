package top.ersut.spring;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import top.ersut.spring.server.UserServer;


class MyMethodInterceptorTest {

    @Test
    void tset() {
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
        UserServer userServerProxy = (UserServer)Enhancer.create(UserServer.class,null,new MyMethodInterceptor());
        System.out.println("父类："+userServerProxy.getClass().getSuperclass().getSimpleName());
        userServerProxy.add();
        System.out.println();
        userServerProxy.all();



    }
}