package top.ersut.protocol.chat.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class ChatRequestMessage extends Message {
    private String content;
    private String to;

    public ChatRequestMessage() {
    }

    public ChatRequestMessage(String to, String content) {
        this.to = to;
        this.content = content;
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.ChatRequestMessage;
    }

    @Override
    public String toString() {
        return "ChatRequestMessage{" +
                "content='" + content + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
