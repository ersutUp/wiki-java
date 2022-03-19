package xyz.ersut.security.securitydemo;

import io.jsonwebtoken.lang.Objects;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.ObjectUtils;
import xyz.ersut.security.securitydemo.pojo.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
//开启AOP
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class SecurityDemoApplication {

    /**用户数据(用户表)*/
    public static List<User> userAll = new ArrayList<User>(){
        {
            add(new User(1L,"root","$2a$10$/ZRxNNPOmWr8NgneZRA6EeE7O4.SRev9GvtttvbAc4tVrI4nUwt8.",1,18));//密码1234
            add(new User(2L,"test","$2a$10$w2ARsfOr0gQowgPW5P3U6OnjVjz4lBQy.P3NV9wWq1gDqGPcd0DqK",1,18));//密码12345
            add(new User(3L,"user","$2a$10$WqBtqXdaasqhyPNKsyPw9Oy4k3a9tyHKX4eLdnH2qJmzRR4m7u9AW",1,20));//密码123
            add(new User(4L,"admin","$2a$10$mWfqkVN2xv2dF8kSLyL.b.ss/OgcwC9h/Htz7N1u71hdt1fKyLNni",0,25));//密码123456
        }
    };
    //用户id索引
    public static Map<Long,User> userById = userAll.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    //用户username索引
    public static Map<String,User> userByUsername = userAll.stream().collect(Collectors.toMap(User::getUserName, Function.identity()));

    /** 权限表（菜单表）*/
    public static List<Menu> menuAll = new ArrayList<Menu>(){
        {
            add(Menu.builder().id(1L).menuName("测试hello_human").perms("sys:helloHuman:list").delFlag(0).status(0).visible(0).build());
            add(Menu.builder().id(2L).menuName("测试hello_world").perms("sys:helloWorld:list").delFlag(0).status(0).visible(0).build());
        }
    };
    //菜单id索引
    public static Map<Long,Menu> menuById = menuAll.stream().collect(Collectors.toMap(Menu::getId,Function.identity()));

    /** 角色表*/
    public static List<Role> roleAll = new ArrayList<Role>(){
        {
            add(Role.builder().id(1L).name("管理员").delFlag(0).status(0).build());
            add(Role.builder().id(2L).name("用户").delFlag(0).status(0).build());
        }
    };
    //角色id索引
    public static Map<Long,Role> roleById = roleAll.stream().collect(Collectors.toMap(Role::getId,Function.identity()));

    /** 权限角色中间表*/
    public static List<RoleMenu> roleMenuAll = new ArrayList<RoleMenu>(){
        {
            add(RoleMenu.builder().roleId(1L).menuId(1L).build());
            add(RoleMenu.builder().roleId(1L).menuId(2L).build());

            add(RoleMenu.builder().roleId(2L).menuId(2L).build());
        }
    };
    //权限角色 角色id索引
    public static Map<Long,List<RoleMenu>> roleMenuByRoleId = roleMenuAll.stream().collect(Collectors.groupingBy(RoleMenu::getRoleId));


    /** 角色用户中间表 */
    public static List<RoleUser> roleUserAll = new ArrayList<RoleUser>(){
        {
            add(RoleUser.builder().userId(1L).roleId(1L).build());
            add(RoleUser.builder().userId(1L).roleId(2L).build());

            add(RoleUser.builder().userId(2L).roleId(2L).build());

            add(RoleUser.builder().userId(3L).roleId(2L).build());

            add(RoleUser.builder().userId(4L).roleId(1L).build());
        }
    };
    //角色用户 用户id索引
    public static Map<Long,List<RoleUser>> roleUserByUserId = roleUserAll.stream().collect(Collectors.groupingBy(RoleUser::getUserId));


    /**
     * 根据用户id获取权限
     * @param userId
     * @return
     */
    public static String[] selectPermsByUserId(Long userId){
        /** 查询角色权限 **/
        List<RoleMenu> roleMenuList = new ArrayList<>();
        List<RoleUser> roleUsers = roleUserByUserId.get(userId);
        if(roleUsers == null){
            return null;
        }
        roleUsers.forEach(roleUser -> {
            List<RoleMenu> tmp = roleMenuByRoleId.get(roleUser.getRoleId());
            if(!ObjectUtils.isEmpty(tmp)){
                roleMenuList.addAll(tmp);
            }
        });
        if(ObjectUtils.isEmpty(roleMenuList)){
            return null;
        }
        //权限去重
        Set<Long> menuIds = roleMenuList.stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());

        /**查询权限并封装**/
        List<String> menuList = new ArrayList<>();
        menuIds.stream().forEach(menuId -> {
            menuList.add(menuById.get(menuId).getPerms());
        });

        //返回
        return menuList.toArray(new String[menuList.size()]);
    }

    public static void main(String[] args) {
        SpringApplication.run(SecurityDemoApplication.class, args);
    }

}
