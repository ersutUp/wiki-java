package xyz.ersut.security.securitydemo.config.security;

import java.util.Set;

public interface AuthUser {

    String[]  getPermissions();

    void setCurrentPermissions(Set<String> currentPermissions);
}
