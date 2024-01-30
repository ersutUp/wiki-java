package top.ersut.protocol.chat.server.service;

import top.ersut.protocol.chat.server.entity.User;

/**
 * 用户管理接口
 */
public interface UserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回 用户信息, 否则返回 null
     */
    User login(String username, String password);
}
