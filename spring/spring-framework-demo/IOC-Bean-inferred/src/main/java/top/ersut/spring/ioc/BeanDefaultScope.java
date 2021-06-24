package top.ersut.spring.ioc;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class BeanDefaultScope {

    public boolean callInit = false;
    public boolean callDestroy = false;

    @PostConstruct
    public void init(){
        callInit = true;
        System.out.println(this+" call init");
    }

    public void close(){
        callDestroy = true;
        System.out.println(this+" call destroy");
    }
}
