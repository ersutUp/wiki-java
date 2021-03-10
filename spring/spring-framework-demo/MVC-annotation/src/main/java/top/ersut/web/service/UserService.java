package top.ersut.web.service;

import top.ersut.web.pojo.User;

import java.util.List;

public interface UserService {

    /** 根据id查询 */
    User queryByID(Long id);

    /** 查询所有 */
    List<User> queryAll();

    /** 添加 */
    User saveUser(User user);

    /** 添加 */
    List<User> saveUsers(List<User> users);

}
