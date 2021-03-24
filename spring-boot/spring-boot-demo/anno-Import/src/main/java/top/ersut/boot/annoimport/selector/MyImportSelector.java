package top.ersut.boot.annoimport.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import top.ersut.boot.annoimport.pojo.Student;
import top.ersut.boot.annoimport.pojo.Teacher;

public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //将 Teacher 和 Student 注册为bean，名称为类的全路径，即 top.ersut.boot.annoimport.pojo.Teacher 和 top.ersut.boot.annoimport.pojo.Student
        return new String[]{Teacher.class.getName(), Student.class.getName()};
    }
}
