package top.ersut.protocol.chat.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupQuitResponseMessage extends AbstractResponseMessage {
    public GroupQuitResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.GroupQuitResponseMessage;
    }
}
