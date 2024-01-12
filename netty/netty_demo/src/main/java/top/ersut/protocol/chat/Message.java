package top.ersut.protocol.chat;

import lombok.Data;

import javax.xml.ws.handler.MessageContext;
import java.util.Random;

@Data
public abstract class Message {

    //版本号
    private byte version = 0x01;

    //序列化方式
    private SerializationTypeEnum serialization;

    //序号
    private int sequenceId;

    public Message(){
        this.sequenceId = new Random().nextInt();
        this.serialization = SerializationTypeEnum.JSON;
    }

    //获取消息类型的抽象方法
    public abstract MessageTypeEnum getMessageType();

}
