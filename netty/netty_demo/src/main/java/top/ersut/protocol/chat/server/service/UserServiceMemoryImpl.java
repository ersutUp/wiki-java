package top.ersut.protocol.chat.server.service;

import top.ersut.protocol.chat.server.entity.User;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService {

    @Override
    public User login(String username, String password) {

        final User user = allUserMap.get(username);
        if (user != null && Objects.equals(user.getPassword(),password))
        {
            return user;
        }

        return null;
    }


    private Map<String,User> allUserMap = new ConcurrentHashMap<>();
    {
        allUserMap.put("zhangsan",new User("zhangsan","123"));
        allUserMap.put("lisi", new User("lisi","123"));
        allUserMap.put("wangwu", new User("wangwu","123"));
        allUserMap.put("zhaoliu", new User("zhaoliu","123"));
        allUserMap.put("ersut", new User("ersut","123"));
    }
}
