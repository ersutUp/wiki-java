package xyz.ersut.security.securitydemo.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色用户中间表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleUser {
    private Long roleId;
    private Long userId;
}
