package xyz.ersut.security.securitydemo.config.security.openapi.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenAPIPrincipal {

    private String appId;

    private String params;

    private String timestamp;

}
