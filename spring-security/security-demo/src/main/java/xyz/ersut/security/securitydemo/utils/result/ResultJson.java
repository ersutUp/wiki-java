package xyz.ersut.security.securitydemo.utils.result;

import lombok.*;
import xyz.ersut.security.securitydemo.utils.result.code.Resultcode;

@Data
public class ResultJson<T> {

    public ResultJson(){

    }

    public ResultJson(Resultcode resultcode, T data){
        this.code = resultcode.getCode();
        this.message = resultcode.getMessage();
        this.data = data;
    }

    public ResultJson(Resultcode resultcode){
        this.code = resultcode.getCode();
        this.message = resultcode.getMessage();
    }

    public ResultJson(Resultcode resultcode,String msg){
        this.code = resultcode.getCode();
        this.message = msg;
    }

    public static ResultJson generateResultJson(Resultcode resultcode){
        return new ResultJson(resultcode);
    }

    public static <T> ResultJson<T> generateResultJson(Resultcode resultcode,T data){
        return new ResultJson(resultcode,data);
    }

    public int code;

    public String message;

    public T data;

    public ResultJson<T> setData(T data){
        this.data = data;
        return this;
    }

}
