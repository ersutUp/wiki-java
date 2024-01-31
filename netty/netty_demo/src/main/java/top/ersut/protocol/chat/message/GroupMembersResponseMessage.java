package top.ersut.protocol.chat.message;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(callSuper = true)
public class GroupMembersResponseMessage extends AbstractResponseMessage {

    private Set<String> members;

    public GroupMembersResponseMessage(Set<String> members) {
        this.members = members;
    }

    public GroupMembersResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.GroupMembersResponseMessage;
    }
}
