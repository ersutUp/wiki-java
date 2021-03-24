package top.ersut.boot.annoimport.definition;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import top.ersut.boot.annoimport.pojo.Student;
import top.ersut.boot.annoimport.pojo.Teacher;
import top.ersut.boot.annoimport.pojo.User;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //注册bean
        registry.registerBeanDefinition("userDefinition",new RootBeanDefinition(User.class));
        registry.registerBeanDefinition("studentDefinition",new RootBeanDefinition(Student.class));
        registry.registerBeanDefinition("teacherDefinition",new RootBeanDefinition(Teacher.class));
    }
}
