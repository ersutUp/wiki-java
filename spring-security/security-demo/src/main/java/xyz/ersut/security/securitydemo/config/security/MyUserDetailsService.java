package xyz.ersut.security.securitydemo.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.SecurityDemoApplication;
import xyz.ersut.security.securitydemo.pojo.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户
        User user = SecurityDemoApplication.userByUsername.get(username);
        if(ObjectUtils.isEmpty(user)){
            throw new RuntimeException("用户不存在");
        }

        //todo 查询权限
        List<String> role = Arrays.asList("teacher","student");

        return LoginUser.builder().user(user).permissions(role).build();
    }
}