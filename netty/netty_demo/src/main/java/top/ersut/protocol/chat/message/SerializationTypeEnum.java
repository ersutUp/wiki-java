package top.ersut.protocol.chat.message;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public enum SerializationTypeEnum implements MessageSerializar {
    Json((byte) 0x00){
        Gson gson = new Gson();
        @Override
        public <T extends Message> Message deserializer(Class<T> clazz, byte[] msg) {
            String msgStr = new String(msg, StandardCharsets.UTF_8);
            Message message = gson.fromJson(msgStr, clazz);
            return (T)message;
        }

        @Override
        public byte[] serializer(Message message) {
            return gson.toJson(message).getBytes(StandardCharsets.UTF_8);
        }
    },
    Java((byte) 0x01) {
        @Override
        public <T extends Message> Message deserializer(Class<T> clazz, byte[] msg) {
            try {
                // 处理内容
                final ByteArrayInputStream bis = new ByteArrayInputStream(msg);
                final ObjectInputStream ois = new ObjectInputStream(bis);

                // 转成 Message类型
                T message = (T) ois.readObject();

                return message;
            } catch (IOException | ClassNotFoundException e) {

                throw new RuntimeException("反序列化算法失败！");
            }

        }

        @Override
        public byte[] serializer(Message message) {
            try {
                // 处理内容 用对象流包装字节数组 并写入
                ByteArrayOutputStream bos = new ByteArrayOutputStream(); // 访问数组
                ObjectOutputStream oos = new ObjectOutputStream(bos);    // 用对象流 包装
                oos.writeObject(message);

                return bos.toByteArray();

            } catch (IOException e) {
                throw new RuntimeException("序列化算法失败！", e);
            }
        }
    };

    @Getter
    byte val;
    SerializationTypeEnum(byte val){
        this.val = val;
    }


    static Map<Byte,SerializationTypeEnum> enumByVal = null;
    //根据序列化类型查找对应的序列化类
    public static SerializationTypeEnum getEnumByVal(byte val){
        if(enumByVal == null){
            enumByVal = new HashMap<>();
            for (SerializationTypeEnum value : SerializationTypeEnum.values()) {
                enumByVal.put(value.val,value);
            }
        }

        return enumByVal.get(val);
    }
}
