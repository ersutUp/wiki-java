package xyz.ersut.security.securitydemo.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public abstract class AbstractAuthUser implements AuthUser, UserDetails {

    private String[] permissions;

    /**
     * 用户当前访问所匹配的权限
     * 便于在开发中使用
     */
    @JsonIgnore
    private Set<String> currentPermissions;

    @JsonIgnore
    private List<GrantedAuthority> grantedAuthorityList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(grantedAuthorityList != null){
            return grantedAuthorityList;
        }

        grantedAuthorityList = Arrays.stream(getPermissions()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return grantedAuthorityList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
