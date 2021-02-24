package top.ersut.spring.ioc.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

//如果不设置bean的名称，那么取类名将首字母小写作为Bean的名称，也就是userDao
@Repository
public class UserDao {

    @Value("wang")
    private String name;

    public String getName() {
        System.out.println("UserDao.getName()");
        return name;
    }
}
