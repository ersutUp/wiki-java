package top.ersut.boot.annoimport.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.ersut.boot.annoimport.definition.MyImportBeanDefinitionRegistrar;
import top.ersut.boot.annoimport.selector.MyImportSelector;

@Import(MyImportBeanDefinitionRegistrar.class)
@Configuration
public class ImportBeanDefinitionRegistrarDemo {
}
