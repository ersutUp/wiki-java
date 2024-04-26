package xyz.ersut.security.securitydemo.config.security.jwt;

import cn.hutool.crypto.SecureUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderSha1 implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return SecureUtil.sha1(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return SecureUtil.sha1(rawPassword.toString()).equals(encodedPassword);
    }
}