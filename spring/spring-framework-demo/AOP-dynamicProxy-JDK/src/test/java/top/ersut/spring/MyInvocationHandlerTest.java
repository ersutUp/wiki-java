package top.ersut.spring;

import org.junit.jupiter.api.Test;
import top.ersut.spring.server.UserServer;
import top.ersut.spring.server.UserServerImpl;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class MyInvocationHandlerTest {

    @Test
    void tset() {
        //不使用代理
        System.out.println("不使用代理\n");
        UserServer userServer = new UserServerImpl();
        userServer.add();
        System.out.println();
        userServer.all();

        System.out.println("----------------------");

        //使用代理
        System.out.println("使用代理\n");
        UserServer userServerProxy = (UserServer) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserServer.class},new MyInvocationHandler(new UserServerImpl()));
        userServerProxy.add();
        System.out.println();
        userServerProxy.all();
        System.out.println();

    }
}