package top.ersut.spring.ioc;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Data
@Component
public class ApplicationDemo {

    @Autowired
    private ApplicationContext applicationContext;

    public BeanScopePrototype getBeanScopePrototype(){
        return applicationContext.getBean(BeanScopePrototype.class);
    }

}
