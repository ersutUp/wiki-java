package top.ersut.spring.server;

public class UserServerImpl implements UserServer {
    @Override
    public void add() {
        System.out.println(this.getClass().getSimpleName()+".add:run");
    }

    @Override
    public void all() {
        System.out.println(this.getClass().getSimpleName()+".all:run");
    }
}
