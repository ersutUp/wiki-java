package xyz.ersut.security.securitydemo.config.security.expression;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.config.security.LoginUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("cex")
public class CustomExpression {


    public final boolean hasAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

    /**
     * 匹配权限
     * 将匹配的所有权限存入{@link LoginUser}的currentPermissions属性
     * @param authorities 支持正则表达式
     * @return
     */
    public final boolean hasAnyAuthority(String... authorities) {
        //当前用户获取权限
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String[] permissions = loginUser.getPermissions();

        if (ObjectUtils.isEmpty(permissions)) {
            return false;
        }


        //存储当前用户所有匹配的权限
        Set<String> currentPermissions = new HashSet<>();
        //权限匹配
        for (String auth:authorities) {
            //尚不确定会不会拉低性能
            Set<String> collect = Arrays.stream(permissions).filter(permission -> permission.matches(auth)).collect(Collectors.toSet());
            if(!ObjectUtils.isEmpty(collect)){
                currentPermissions.addAll(collect);
            }
        }

        if(ObjectUtils.isEmpty(currentPermissions)){
            return false;
        }
        //放入LoginUser
        loginUser.setCurrentPermissions(currentPermissions);

        return true;
    }

}
