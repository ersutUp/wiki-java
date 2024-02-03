package top.ersut.protocol.chat;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.config.Config;
import top.ersut.protocol.chat.message.Message;
import top.ersut.protocol.chat.message.MessageTypeEnum;
import top.ersut.protocol.chat.message.SerializationTypeEnum;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 标记为线程安全的（可共享的）{@link ChannelHandler.Sharable }
 */
@Slf4j
@ChannelHandler.Sharable
public class ChatMessageCustomCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    static final SerializationTypeEnum serializationTypeEnum;
    static {
        String serializar = Config.getSerializar();
        serializationTypeEnum = SerializationTypeEnum.valueOf(serializar);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {

        ByteBuf bufferOut = ctx.alloc().buffer();
        //魔数
        bufferOut.writeBytes(new byte[]{1, 2, 3, 4});
        //版本号
        bufferOut.writeByte(msg.getVersion());
        //序列化方式
        bufferOut.writeByte(serializationTypeEnum.getVal());
        //消息类型
        bufferOut.writeByte(msg.getMessageType().getType());
        //序号
        bufferOut.writeInt(msg.getSequenceId());

        //补充字节，消息头部凑齐16字节
        bufferOut.writeByte(0);

        //序列化
        byte[] msgByte = serializationTypeEnum.serializer(msg);

        //数据长度
        int length = msgByte.length;
        bufferOut.writeInt(length);

        //消息内容
        bufferOut.writeBytes(msgByte);

        out.add(bufferOut);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        //魔数
        byte magic[] = new byte[4];
        in.readBytes(magic);
        //版本号
        byte version = in.readByte();
        //序列化方式
        byte serialization = in.readByte();
        //消息类型
        byte msgType = in.readByte();
        //序号
        int sequenceId = in.readInt();
        //补充字节
        in.readByte();

        //长度
        int length = in.readInt();

        if (length != 0){
            byte[] content = new byte[length];
            in.readBytes(content);

            //消息类型对象
            Class<Message> classByType = MessageTypeEnum.getClassByType(msgType);
            //反序列化消息
            Message message = SerializationTypeEnum.getEnumByVal(serialization).deserializer(classByType,content);

            log.info("收到数据:[{}]",message);
            //将读取的内容传给下一个处理器
            out.add(message);
        } else {
            log.info("长度值与数据不匹配");
        }
    }
}
