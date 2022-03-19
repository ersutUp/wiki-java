package xyz.ersut.security.securitydemo.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Menu {

    private Long id;

    /**
     * 菜单名
     */
    private String menuName;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单状态（0显示 1隐藏）
     */
    private Integer visible;

    /**
     * 菜单状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 是否删除（0未删除 1已删除）
     */
    private Integer delFlag;

}
