package xyz.ersut.security.securitydemo.exception.login;

import xyz.ersut.security.securitydemo.exception.base.BaseException;
import xyz.ersut.security.securitydemo.exception.base.BaseRuntimeException;

public class LoginException extends BaseRuntimeException {

    public LoginException(String message){
        super(message);
    }

}
