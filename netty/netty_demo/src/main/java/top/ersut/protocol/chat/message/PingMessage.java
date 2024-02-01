package top.ersut.protocol.chat.message;

public class PingMessage extends Message {
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.PingMessage;
    }
}
