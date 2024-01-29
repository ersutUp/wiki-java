package top.ersut.protocol.chat.message;

/**
 * 消息序列化接口
 */
public interface MessageSerializar {

    <T extends Message> Message deserializer(Class<T> clazz,byte[] msg);

    byte[] serializer(Message message);
}
