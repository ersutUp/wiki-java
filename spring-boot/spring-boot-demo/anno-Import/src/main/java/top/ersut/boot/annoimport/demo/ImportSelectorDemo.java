package top.ersut.boot.annoimport.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.ersut.boot.annoimport.selector.MyImportSelector;

@Import(MyImportSelector.class)
@Configuration
public class ImportSelectorDemo {
}
