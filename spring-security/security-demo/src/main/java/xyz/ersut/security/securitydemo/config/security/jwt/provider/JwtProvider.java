package xyz.ersut.security.securitydemo.config.security.jwt.provider;

import cn.hutool.Hutool;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.security.provider.SHA;
import xyz.ersut.security.securitydemo.SecurityDemoApplication;
import xyz.ersut.security.securitydemo.config.security.jwt.PasswordEncoderSha1;
import xyz.ersut.security.securitydemo.config.security.jwt.pojo.JwtInfo;
import xyz.ersut.security.securitydemo.config.security.jwt.token.JwtToken;
import xyz.ersut.security.securitydemo.pojo.entity.User;
import xyz.ersut.security.securitydemo.utils.JwtUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtProvider implements AuthenticationProvider {

    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;

    public JwtProvider(PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authentication;
        String password = jwtToken.getCredentials();
        String account = jwtToken.getPrincipal();
        //获取用户 DB
        User user = SecurityDemoApplication.userByUsername.get(account);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("账号不存在");
        }
        //验证密码
        boolean matches = this.passwordEncoder.matches(password, user.getPassword());

        if (matches) {
            //验证成功
            //获取权限
            Set<String> permissions = new HashSet<>();
            // DB
            String[] userPermissions = SecurityDemoApplication.selectPermsByUserId(user.getId());
            if (userPermissions != null && userPermissions.length > 0) {
                 permissions = new HashSet<>(Arrays.asList(userPermissions));
            }

            //生成token 并放入权限
            JwtInfo jwtInfo = JwtInfo.builder().userId(user.getId()).permissions(permissions).build();
            String jwt = JwtUtil.createJWT(objectMapper.writeValueAsString(jwtInfo) , 60 * 60 * 1000L);

            return new JwtToken(jwt, permissions);
        } else {
            throw new BadCredentialsException("密码错误");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtToken.class.isAssignableFrom(authentication);
    }
}
