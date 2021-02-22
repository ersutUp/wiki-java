package top.ersut.spring.ioc;

import org.springframework.beans.factory.FactoryBean;

import java.util.*;

public class UserFactory implements FactoryBean<User> {

    private String impl;

    public void setImpl(String impl) {
        this.impl = impl;
    }

    @Override
    public User getObject() throws Exception {
        User user = null;

        if(Objects.equals(impl,"student")){
            user = new Student();
        } else if(Objects.equals(impl,"teacher")) {
            user = new Teacher();
        }

        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
