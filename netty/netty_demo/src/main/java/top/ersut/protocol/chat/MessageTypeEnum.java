package top.ersut.protocol.chat;

public enum MessageTypeEnum {
    Login((byte) 0x00),

    ;

    byte val;
    MessageTypeEnum(byte val){
        this.val = val;
    }
}
