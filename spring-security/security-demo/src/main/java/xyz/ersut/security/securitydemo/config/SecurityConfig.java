package xyz.ersut.security.securitydemo.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.SecurityDemoApplication;
import xyz.ersut.security.securitydemo.pojo.entity.User;

import java.util.Collection;

@Configuration
public class SecurityConfig {

    //配置 用户读取的位置（数据库还是缓存或者内存）
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }

    //密码的加密解密
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public class UserDetailsServiceImpl implements UserDetailsService{

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            //查询用户
            User user = SecurityDemoApplication.userByUsername.get(username);
            if(ObjectUtils.isEmpty(user)){
                throw new RuntimeException("用户不存在");
            }

            //todo 查询权限

            return LoginUser.builder().user(user).build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginUser implements UserDetails{

        private User user;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
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

}
