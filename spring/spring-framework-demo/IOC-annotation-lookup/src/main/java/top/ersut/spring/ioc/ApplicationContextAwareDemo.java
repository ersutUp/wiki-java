package top.ersut.spring.ioc;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Data
@Component
public class ApplicationContextAwareDemo implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public BeanScopePrototype getBeanScopePrototype(){
        return applicationContext.getBean(BeanScopePrototype.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
