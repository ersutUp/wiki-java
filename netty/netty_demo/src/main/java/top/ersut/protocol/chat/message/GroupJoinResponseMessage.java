package top.ersut.protocol.chat.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupJoinResponseMessage extends AbstractResponseMessage {

    public GroupJoinResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.GroupJoinResponseMessage;
    }
}
