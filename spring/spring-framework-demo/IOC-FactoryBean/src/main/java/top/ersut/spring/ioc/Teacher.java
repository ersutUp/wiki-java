package top.ersut.spring.ioc;

public class Teacher implements User {
    @Override
    public void say() {
        System.out.println("my is Teacher");
    }
}
