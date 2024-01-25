package top.ersut.protocol.chat.message;

import lombok.Getter;

public enum SerializationTypeEnum {
    JSON((byte) 0x00),

    ;

    @Getter
    byte val;
    SerializationTypeEnum(byte val){
        this.val = val;
    }
}
