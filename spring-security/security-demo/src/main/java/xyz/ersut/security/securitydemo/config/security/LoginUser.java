package xyz.ersut.security.securitydemo.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.ersut.security.securitydemo.pojo.entity.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUser implements UserDetails {

    private User user;

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

        grantedAuthorityList = Arrays.stream(permissions).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
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