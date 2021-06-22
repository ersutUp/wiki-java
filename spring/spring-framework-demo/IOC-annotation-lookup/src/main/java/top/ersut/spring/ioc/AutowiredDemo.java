package top.ersut.spring.ioc;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class AutowiredDemo {

    @Autowired
    private BeanScopePrototype beanScopePrototype;

}
