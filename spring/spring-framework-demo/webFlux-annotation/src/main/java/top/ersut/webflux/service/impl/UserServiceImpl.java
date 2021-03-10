package top.ersut.webflux.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.ersut.webflux.pojo.User;
import top.ersut.webflux.service.UserService;

import java.util.HashMap;
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
    public Mono<User> queryByID(Long id) {
        return Mono.justOrEmpty(users.get(id));
    }

    /**
     * 查询所有
     */
    @Override
    public Flux<User> queryAll() {
        return Flux.fromIterable(users.values());
    }

    /**
     * 添加
     *
     * @param userMono
     */
    @Override
    public Mono<User> saveUser(Mono<User> userMono) {
        return userMono.doOnNext((e)->{addUser(e);});
    }

    /**
     * 添加
     *
     * @param usersFlux
     */
    @Override
    public Flux<User> saveUsers(Flux<User> usersFlux) {
        return usersFlux.doOnNext((e)->{
            addUser(e);
        });
    }
}
