package xyz.ersut.security.securitydemo.config.security.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.SecurityDemoApplication;
import xyz.ersut.security.securitydemo.config.security.jwt.LoginUser;
import xyz.ersut.security.securitydemo.pojo.entity.User;

public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户
        User user = SecurityDemoApplication.userByUsername.get(username);
        if(ObjectUtils.isEmpty(user)){
            throw new UsernameNotFoundException("用户不存在");
        }

        //查询权限
        String[] permissions = SecurityDemoApplication.selectPermsByUserId(user.getId());

        LoginUser loginUser = LoginUser.builder().user(user).build();
        loginUser.setPermissions(permissions);
        return loginUser;
    }
}