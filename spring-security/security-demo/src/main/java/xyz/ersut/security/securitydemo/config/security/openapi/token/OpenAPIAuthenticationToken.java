package xyz.ersut.security.securitydemo.config.security.openapi.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;
import xyz.ersut.security.securitydemo.config.security.openapi.OpenAPIUser;
import xyz.ersut.security.securitydemo.pojo.entity.Application;

import java.util.Collection;

public class OpenAPIAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    private String credentials;

    public OpenAPIAuthenticationToken(OpenAPIPrincipal principal, String sign) {
        super(null);
        this.principal = principal;
        this.credentials = sign;
        setAuthenticated(false);
    }

    public OpenAPIAuthenticationToken(OpenAPIUser principal, String sign,
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = sign;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated,
                "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

}