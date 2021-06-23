package top.ersut.spring.ioc;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BeanScopePrototype {

    public boolean callInit = false;
    public boolean callDestroy = false;

    @PostConstruct
    public void init(){
        callInit = true;
        System.out.println(this+" call init");
    }

    @PreDestroy
    public void destroy(){
        callDestroy = true;
        System.out.println(this+" call destroy");
    }

}
