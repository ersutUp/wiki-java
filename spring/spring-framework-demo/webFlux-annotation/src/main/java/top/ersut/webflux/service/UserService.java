package top.ersut.webflux.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.ersut.webflux.pojo.User;

public interface UserService {

    /** 根据id查询 */
    Mono<User> queryByID(Long id);

    /** 查询所有 */
    Flux<User> queryAll();

    /** 添加 */
    Mono<User> saveUser(Mono<User> userMono);

    /** 添加 */
    Flux<User> saveUsers(Flux<User> usersFlux);

}
