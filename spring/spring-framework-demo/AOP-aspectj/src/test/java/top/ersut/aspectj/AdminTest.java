package top.ersut.aspectj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdminTest {

    @Test
    void login() {

        //私有方法可以代理
        Admin admin = new Admin();

        System.out.println();

        Boolean isLogin = admin.login("wang");
        Assertions.assertTrue(isLogin);

        System.out.println();

        //静态方法可以代理
        Admin.staticMethod();

    }
}