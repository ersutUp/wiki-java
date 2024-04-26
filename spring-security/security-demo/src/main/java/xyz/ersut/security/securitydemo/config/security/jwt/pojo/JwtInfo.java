package xyz.ersut.security.securitydemo.config.security.jwt.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtInfo {

    private Set<String> permissions;

    private Long userId;

}
