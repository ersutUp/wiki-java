package top.ersut.spring.ioc;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class BeanDefaultScope {

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
