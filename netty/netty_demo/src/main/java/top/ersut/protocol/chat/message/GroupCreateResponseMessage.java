package top.ersut.protocol.chat.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupCreateResponseMessage extends AbstractResponseMessage {

    public GroupCreateResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.GroupCreateResponseMessage;
    }
}
