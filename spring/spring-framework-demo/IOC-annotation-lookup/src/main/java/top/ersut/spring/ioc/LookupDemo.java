package top.ersut.spring.ioc;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LookupDemo {

    @Lookup
    public BeanScopePrototype getBeanScopePrototype(){
        return null;
    }

}
