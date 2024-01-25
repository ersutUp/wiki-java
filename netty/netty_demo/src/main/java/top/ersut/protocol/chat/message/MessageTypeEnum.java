package top.ersut.protocol.chat.message;

import lombok.Getter;

public enum MessageTypeEnum {
    LoginRequestMessage((byte) 0x00,LoginRequestMessage.class),
    ChatRequestMessage((byte) 0x01,ChatRequestMessage.class),
    GroupChatRequestMessage((byte) 0x02,GroupChatRequestMessage.class),
    GroupCreateRequestMessage((byte) 0x03,GroupCreateRequestMessage.class),
    GroupJoinRequestMessage((byte) 0x04,GroupJoinRequestMessage.class),
    GroupMembersRequestMessage((byte) 0x05,GroupMembersRequestMessage.class),
    GroupQuitRequestMessage((byte) 0x06,GroupQuitRequestMessage.class),


    LoginResponseMessage((byte) 0x07,LoginResponseMessage.class),
    ChatResponseMessage((byte) 0x08,ChatResponseMessage.class),
    GroupChatResponseMessage((byte) 0x09,GroupChatResponseMessage.class),
    GroupCreateResponseMessage((byte) 0x0A,GroupCreateResponseMessage.class),
    GroupJoinResponseMessage((byte) 0x0B,GroupJoinResponseMessage.class),
    GroupMembersResponseMessage((byte) 0x0C,GroupMembersResponseMessage.class),
    GroupQuitResponseMessage((byte) 0x0D,GroupQuitResponseMessage.class),

    ;

    @Getter
    byte type;
    @Getter
    Class clazz;
    <T extends Message> MessageTypeEnum (byte type, Class<T> clazz){
        this.type = type;
        this.clazz = clazz;
    }

    static MessageTypeEnum[] values = null;
    //根据消息类型查找对应的class
    public static <T extends Message> Class<T> getClassByType(byte type){
        if(values == null){
            values = values();
        }

        for (MessageTypeEnum value : values) {
            if (value.type == type) {
                return value.clazz;
            }
        }

        return null;
    }
}
