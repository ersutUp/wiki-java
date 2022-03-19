package xyz.ersut.security.securitydemo.exception.base;

public class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException(){
        super();
    }
    public BaseRuntimeException(String message){
        super(message);
    }

}
