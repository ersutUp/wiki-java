package top.ersut.web.service.impl;

import org.springframework.stereotype.Service;
import top.ersut.web.pojo.User;
import top.ersut.web.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {


    private static Long AUTO_INCREMENT_NEXT_ID = 1L;
    private final static Map<Long,User> users = new HashMap<>();

    //添加用户
    private static Long addUser(User user){
        user.setID(AUTO_INCREMENT_NEXT_ID++);
        users.put(user.getID(),user);
        return user.getID();
    }

    public UserServiceImpl() {
        //初始化数据
        addUser(new User("wang"));
        addUser(new User("ersut"));
    }

    /**
     * 根据id查询
     *
     * @param id
     */
    @Override
    public User queryByID(Long id) {
        return users.get(id);
    }

    /**
     * 查询所有
     */
    @Override
    public List<User> queryAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * 添加
     *
     * @param user
     */
    @Override
    public User saveUser(User user) {
        addUser(user);
        return user;
    }

    /**
     * 添加
     *
     * @param users
     */
    @Override
    public List<User> saveUsers(List<User> users) {
        for (User user : users) {
            addUser(user);
        }
        return users;
    }
}
