package top.ersut.spring.ioc.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import top.ersut.spring.ioc.dao.UserDao;

import javax.annotation.Resource;

//可以设置bean的名称为userServer2
@Service("userServer2")
public class UserServer {
    /**可以放在属性上，根据类型注入 */
    @Autowired
    private UserDao userDao;

    private UserDao userDao2;
    /**也可以放在对应的set方法上，根据类型注入*/
    @Autowired
    public void setUserDao2(UserDao userDao2) {
        this.userDao2 = userDao2;
    }

    /** 根据名称注入 */
    @Autowired
    @Qualifier("userDao")
    private UserDao userDao3;

    /** 根据类型注入 */
    @Resource
    private UserDao userDao4;

    /** 根据名称注入 */
    @Resource(name = "userDao")
    private UserDao userDao5;

    public void selectUserName(){
        userDao.getName();
        userDao2.getName();
        userDao3.getName();
        userDao4.getName();
        userDao5.getName();
        System.out.println("UserServer.selectUserName");
    }

}
