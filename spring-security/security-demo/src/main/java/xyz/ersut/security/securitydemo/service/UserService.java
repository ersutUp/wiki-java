package xyz.ersut.security.securitydemo.service;

import xyz.ersut.security.securitydemo.pojo.entity.User;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;

public interface UserService {
    ResultJson login(User user);

    ResultJson logout();
}
