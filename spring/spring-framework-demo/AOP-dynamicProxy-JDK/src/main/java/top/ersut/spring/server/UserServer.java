package top.ersut.spring.server;

public interface UserServer {

    void add();

    void all();

    //静态方法无法代理
    public static void staticMethod() {
        System.out.println(UserServer.class.getSimpleName()+".staticMethod:run");
    }

}
