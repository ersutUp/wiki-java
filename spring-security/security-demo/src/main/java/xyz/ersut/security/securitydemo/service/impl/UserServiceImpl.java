package xyz.ersut.security.securitydemo.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import xyz.ersut.security.securitydemo.config.security.LoginUser;
import xyz.ersut.security.securitydemo.config.security.SecurityConfig;
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
    private Cache<Long, LoginUser> loginUserCache;

    @Override
    public ResultJson login(User user) {
        //获取 AuthenticationManager
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authentication);

        if(Objects.isNull(authenticate)){
            return new ResultJson(ResultSystemCode.AUTH_ERROR,"登录失败");
        }

        //获取数据库中的真实用户
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
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

    @Override
    public ResultJson logout() {
        //获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        //清楚缓存数据
        loginUserCache.invalidate(loginUser.getUser().getId());
        return new ResultJson(ResultSystemCode.SUCCESS,"登出成功");
    }
}
