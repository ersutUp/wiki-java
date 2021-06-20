package top.ersut.spring.ioc;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Serializable;
@Data
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class BeanScopeSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private int num = 1;


    @PostConstruct
    public void init(){
        System.out.println(this+" call init");
    }

    @PreDestroy
    public void destroy(){
        System.out.println(this+" call destroy");
    }

}
