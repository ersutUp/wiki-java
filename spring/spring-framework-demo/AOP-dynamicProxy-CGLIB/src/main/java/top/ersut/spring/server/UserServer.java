package top.ersut.spring.server;

public class UserServer {

    public void add() {
        System.out.println(this.getClass().getSimpleName()+".add:run");
    }

    //静态方法无法代理
    public static void staticMethod() {
        System.out.println(UserServer.class.getSimpleName()+".staticMethod:run");
    }

    //final方法无法代理
    public final void finalMethod() {
        System.out.println(UserServer.class.getSimpleName()+".finalMethod:run");
    }

    public void all() {
        System.out.println(this.getClass().getSimpleName()+".all:run");
    }
}
