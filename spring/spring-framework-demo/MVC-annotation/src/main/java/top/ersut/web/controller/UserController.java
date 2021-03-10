package top.ersut.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ersut.web.pojo.User;
import top.ersut.web.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** 根据id查询 */
    @GetMapping("/{id}")
    public User queryByID(@PathVariable Long id){
        return userService.queryByID(id);
    }

    /** 查询所有 */
    @GetMapping
    public List<User> queryAll(){
        return userService.queryAll();
    }

    /** 添加 */
    @PostMapping
    public User saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    /** 添加 */
    @PostMapping("/batch")
    public List<User> savesUser(@RequestBody List<User> users){
        return userService.saveUsers(users);
    }


}
