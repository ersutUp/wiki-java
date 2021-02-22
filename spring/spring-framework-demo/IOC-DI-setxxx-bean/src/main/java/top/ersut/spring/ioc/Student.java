package top.ersut.spring.ioc;

public class Student {

    private Teacher teacher;

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void myTeacherSay(){
        teacher.say();
    }

}
