package top.ersut.spring.ioc;

public class Student implements User {
    @Override
    public void say() {
        System.out.println("my is student");
    }
}
