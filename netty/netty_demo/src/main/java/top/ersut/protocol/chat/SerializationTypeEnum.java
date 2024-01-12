package top.ersut.protocol.chat;

public enum SerializationTypeEnum {
    JSON((byte) 0x00),

    ;

    byte val;
    SerializationTypeEnum(byte val){
        this.val = val;
    }
}
