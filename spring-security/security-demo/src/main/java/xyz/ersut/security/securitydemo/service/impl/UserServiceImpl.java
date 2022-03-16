package xyz.ersut.security.securitydemo.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import xyz.ersut.security.securitydemo.config.SecurityConfig;
import xyz.ersut.security.securitydemo.pojo.entity.User;
import xyz.ersut.security.securitydemo.service.UserService;
import xyz.ersut.security.securitydemo.utils.JwtUtil;
import xyz.ersut.security.securitydemo.utils.result.ResultJson;
import xyz.ersut.security.securitydemo.utils.result.code.ResultSystemCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Cache<Long, SecurityConfig.LoginUser> loginUserCache;

    @Override
    public ResultJson login(User user) {
        //获取 AuthenticationManager
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate;
        try{
            authenticate = authenticationManager.authenticate(authentication);
        } catch (BadCredentialsException e){
            //账号密码认证失败
            return new ResultJson(ResultSystemCode.AUTH_ERROR,e.getMessage());
        }

        if(Objects.isNull(authenticate)){
            return new ResultJson(ResultSystemCode.AUTH_ERROR,"登录失败");
        }

        //获取数据库中的真实用户
        SecurityConfig.LoginUser loginUser = (SecurityConfig.LoginUser) authenticate.getPrincipal();
        User userDB = loginUser.getUser();
        Long userId = userDB.getId();

        //生成 jwt
        String jwt = JwtUtil.createJWT(userId.toString());
        Map<String,String> data = new HashMap<String, String>(){
            {
                put("token",jwt);
            }
        };
        //放入缓存
        loginUserCache.put(userId,loginUser);

        return ResultJson.generateResultJson(ResultSystemCode.SUCCESS,data);
    }
}
