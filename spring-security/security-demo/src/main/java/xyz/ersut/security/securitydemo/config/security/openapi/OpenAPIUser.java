package xyz.ersut.security.securitydemo.config.security.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import xyz.ersut.security.securitydemo.config.security.AbstractAuthUser;
import xyz.ersut.security.securitydemo.config.security.AuthUser;
import xyz.ersut.security.securitydemo.config.security.openapi.token.OpenAPIPrincipal;
import xyz.ersut.security.securitydemo.pojo.entity.Application;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAPIUser extends AbstractAuthUser {

    private OpenAPIPrincipal openAPIPrincipal;

    private Application application;

    @Override
    public String getPassword() {
        return application.getAppkey();
    }

    @Override
    public String getUsername() {
        return application.getAppid();
    }
}
