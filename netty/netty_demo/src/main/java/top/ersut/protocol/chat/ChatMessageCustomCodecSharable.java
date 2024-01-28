package top.ersut.protocol.chat;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
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
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {

        ByteBuf bufferOut = ctx.alloc().buffer();
        //魔数
        bufferOut.writeBytes(new byte[]{1, 2, 3, 4});
        //版本号
        bufferOut.writeByte(msg.getVersion());
        //序列化方式
        bufferOut.writeByte(msg.getSerialization().getVal());
        //消息类型
        bufferOut.writeByte(msg.getMessageType().getType());
        //序号
        bufferOut.writeInt(msg.getSequenceId());

        //补充字节，消息头部凑齐16字节
        bufferOut.writeByte(0);

        byte msgByte[] = new Gson().toJson(msg).getBytes(StandardCharsets.UTF_8);

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

            //根据序列化方式 处理消息内容
            if(SerializationTypeEnum.JSON.getVal() == serialization){
                Class<Message> classByType = MessageTypeEnum.getClassByType(msgType);
                Message message = deserializer(classByType, content);
                log.info("收到数据:[{}]",message);
                //将读取的内容传给下一个处理器
                out.add(message);
            }
        } else {
            log.info("长度值与数据不匹配");
        }
    }

    private <T extends Message> T deserializer(Class<T> clazz,byte[] content){
        String s = new String(content, StandardCharsets.UTF_8);
        Message message = new Gson().fromJson(s, clazz);
        return (T)message;
    }
}
