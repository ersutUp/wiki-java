package top.ersut.boot.conf.service.impl;

import top.ersut.boot.conf.service.StudentService;

public class StudentServiceImpl implements StudentService {

    private String name;

    public StudentServiceImpl(String name) {
        this.name = name;
    }


    @Override
    public String showName() {
        System.out.println(name);
        return name;
    }
}
