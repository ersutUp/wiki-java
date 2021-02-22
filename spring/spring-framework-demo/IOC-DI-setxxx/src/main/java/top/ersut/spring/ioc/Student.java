package top.ersut.spring.ioc;

public class Student {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void say(){
        System.out.println("my name is "+name);;
    }

}
