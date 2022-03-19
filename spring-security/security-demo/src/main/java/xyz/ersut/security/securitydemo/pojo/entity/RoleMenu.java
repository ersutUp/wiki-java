package xyz.ersut.security.securitydemo.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色权限中间表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleMenu {
    private Long roleId;
    private Long menuId;
}
