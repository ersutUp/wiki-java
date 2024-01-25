package top.ersut.protocol.chat.message;

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


    public LoginRequestMessage(String account,String password){
        this.account = account;
        this.password = password;
    }


    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.LoginRequestMessage;
    }


}
