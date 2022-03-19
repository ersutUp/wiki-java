package xyz.ersut.security.securitydemo.exception.code;

import xyz.ersut.security.securitydemo.exception.base.BaseRuntimeException;
import xyz.ersut.security.securitydemo.utils.result.code.Resultcode;

public class CodeException extends BaseRuntimeException {
    private final Resultcode resultcode;

    //用于回显给页面 非错误信息中的message
    private final String showMessage;

    public CodeException(Resultcode resultcode){
        super("错误消息["+resultcode.getMessage()+"]，对应状态码:["+resultcode.getCode()+"]");
        this.resultcode = resultcode;
        this.showMessage = null;
    }

    public CodeException(Resultcode resultcode,String message){
        super("错误消息["+message+"]，对应状态码:["+resultcode.getCode()+"]");
        this.resultcode = resultcode;
        this.showMessage = message;
    }

    public Resultcode getResultcode() {
        return resultcode;
    }

    public String getShowMessage(){
        return showMessage;
    }

}
