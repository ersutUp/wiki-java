package top.ersut.java.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import top.ersut.java.gson.pojo.Student;
import top.ersut.java.gson.pojo.Teacher;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TimestampTypeAdapterTest {

    @Test
    public void testTypeAdapter(){
        String json = new Gson().toJson(Student.builder().name("学生").createAt(new Date()).updateAt(LocalDateTime.now()).build());
        System.out.println(json);
    }

    @Test
    public void testTypeAdapterFactory(){
        String json = new GsonBuilder()
                .registerTypeAdapterFactory(TimestampTypeAdapter.DATE_FACTORY)
                .registerTypeAdapterFactory(TimestampTypeAdapter.LOCALDATETIME_FACTORY)
                .create()
                .toJson(Teacher.builder().name("老师").createAt(new Date()).updateAt(LocalDateTime.now()).build());
        System.out.println(json);
    }

}