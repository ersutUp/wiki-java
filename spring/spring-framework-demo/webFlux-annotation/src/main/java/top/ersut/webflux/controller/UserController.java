package top.ersut.webflux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.ersut.webflux.pojo.User;
import top.ersut.webflux.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** 根据id查询 */
    @GetMapping("/{id}")
    public Mono<User> queryByID(@PathVariable Long id){
        return userService.queryByID(id);
    }

    /** 查询所有 */
    @GetMapping
    public Flux<User> queryAll(){
        return userService.queryAll();
    }

    /** 添加 */
    @PostMapping
    public Mono<User> saveUser(@RequestBody User user){
        Mono<User> userMono = Mono.just(user);
        return userService.saveUser(userMono);
    }

    /** 添加 */
    @PostMapping("/batch")
    public Flux<User> savesUser(@RequestBody List<User> users){
        Flux<User> usersMono = Flux.fromIterable(users);
        return userService.saveUsers(usersMono);
    }


}
