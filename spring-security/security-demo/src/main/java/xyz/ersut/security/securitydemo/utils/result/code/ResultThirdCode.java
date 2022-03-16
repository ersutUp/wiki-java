package xyz.ersut.security.securitydemo.utils.result.code;

import lombok.Getter;

public enum ResultThirdCode implements Resultcode {

    ERROR(30001,"第三方报错"),
    ;


    @Getter
    private int code;
    @Getter
    private String message;

    ResultThirdCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
