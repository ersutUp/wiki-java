package xyz.ersut.security.securitydemo.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {


    private Long id;

    /**
     * 角色名
     */
    private String name;

    /**
     * 角色状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 是否删除（0未删除 1已删除）
     */
    private Integer delFlag;

}
