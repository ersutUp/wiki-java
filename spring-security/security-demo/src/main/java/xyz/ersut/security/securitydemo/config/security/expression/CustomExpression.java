package xyz.ersut.security.securitydemo.config.security.expression;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.config.security.AuthUser;
import xyz.ersut.security.securitydemo.config.security.jwt.LoginUser;
import xyz.ersut.security.securitydemo.config.security.jwt.pojo.JwtInfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component("cex")
public class CustomExpression {


    public final boolean hasAuthority(String authority) {
        return hasAnyAuthority(authority);
    }

    /**
     * 匹配权限
     * @param authorities 支持正则表达式
     * @return
     */
    public final boolean hasAnyAuthority(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //当前用户获取权限
        Set<String> permissions = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (ObjectUtils.isEmpty(permissions)) {
            return false;
        }

        //权限匹配
        for (String auth:authorities) {
            //尚不确定会不会拉低性能
            Set<String> collect = permissions.stream()
                    .filter(permission -> permission.matches(auth))
                    .collect(Collectors.toSet());
            if(!ObjectUtils.isEmpty(collect)){
                return true;
            }
        }

        return false;
    }

}
