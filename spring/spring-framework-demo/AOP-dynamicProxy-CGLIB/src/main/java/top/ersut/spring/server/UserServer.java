package top.ersut.spring.server;

public class UserServer {

    public void add() {
        System.out.println(this.getClass().getSimpleName()+".add:run");
    }

    public void all() {
        System.out.println(this.getClass().getSimpleName()+".all:run");
    }
}
