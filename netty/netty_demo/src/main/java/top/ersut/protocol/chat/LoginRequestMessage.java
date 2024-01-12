package top.ersut.protocol.chat;

import lombok.Data;

@Data
public class LoginRequestMessage extends Message {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    public LoginRequestMessage(String account,String password,String name){
        this.account = account;
        this.password = password;
        this.name = name;
    }


    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.Login;
    }


}
