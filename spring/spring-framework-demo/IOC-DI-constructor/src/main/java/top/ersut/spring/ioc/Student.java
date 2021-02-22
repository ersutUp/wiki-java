package top.ersut.spring.ioc;

public class Student {

    public Student(String name){
        this.name = name;
    }

    private String name;

    public void say(){
        System.out.println("my name is "+name);;
    }

}
