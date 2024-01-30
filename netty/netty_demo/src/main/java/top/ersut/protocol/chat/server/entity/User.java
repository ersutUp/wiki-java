package top.ersut.protocol.chat.server.entity;

import lombok.Data;

@Data
public class User {

    private String userName;

    private String password;


    public User(String userName,String password){
        this.userName = userName;
        this.password = password;
    }

}
