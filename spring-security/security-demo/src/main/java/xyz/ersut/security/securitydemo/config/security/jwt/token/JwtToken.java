package xyz.ersut.security.securitydemo.config.security.jwt.token;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class JwtToken extends AbstractAuthenticationToken {

    /**
     * 将权限转换为GrantedAuthority
     */
    private static Collection<? extends GrantedAuthority> permission2GrantedAuthority(Set<String> permissions){
        if(Objects.isNull(permissions)){
            return null;
        }
        if(permissions.isEmpty()){
            return null;
        }

        Collection<SimpleGrantedAuthority> authorityCollection = new HashSet(permissions.size());
        for (String permission: permissions) {
            authorityCollection.add(new SimpleGrantedAuthority(permission));
        }
        return authorityCollection;
    }

    private String account;
    private String password;

    @Getter
    private Set<String> permissions;

    /**
     * 授权时使用
     */
    public JwtToken(String jwtToken, Set<String> permissions) {
        super(permission2GrantedAuthority(permissions));
        this.account = jwtToken;
        this.permissions = permissions;
        //无需再次认证
        setAuthenticated(true);
    }

    /**
     * 认证时使用
     */
    public JwtToken(String account,String password) {
        super(null);
        this.account = account;
        this.password = password;
        setAuthenticated(false);
    }

    @Override
    public String getCredentials() {
        return password;
    }

    @Override
    public String getPrincipal() {
        return account;
    }
}
