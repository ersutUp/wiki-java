package xyz.ersut.security.securitydemo.utils.result.code;

import lombok.Getter;

/**
 * @author 王二飞
 */

public enum ResultSystemCode implements Resultcode {


    SUCCESS(20000,"success"),
    ERROR(20001,"网络繁忙，请稍后再操作"),
    NO_API(20002,"No API"),
    LIMIT(20003,"访问受限"),
    PARAM_ERROR(20004,"参数错误"),
    TIMEOUT(20005,"访问超时"),
    ILLEGAL(20006,"非法请求"),
    AUTH_ERROR(20007,"认证失败"),
    PERMISSIONS_ERROR(20008,"权限不足"),

    ;
    @Getter
    private int code;
    @Getter
    private String message;

    ResultSystemCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
