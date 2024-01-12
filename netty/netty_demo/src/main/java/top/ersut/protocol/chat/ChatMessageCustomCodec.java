package top.ersut.protocol.chat;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ChatMessageCustomCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //魔数
        out.writeBytes(new byte[]{1,2,3,4});
        //版本号
        out.writeByte(msg.getVersion());
        //序列化类型
        out.writeByte(msg.getSerialization().val);
        //消息类型
        out.writeByte(msg.getMessageType().val);
        //序号
        out.writeInt(msg.getSequenceId());

        //补充字节，消息头部凑齐16字节
        out.writeByte(0);

        byte msgByte[] = new Gson().toJson(msg).getBytes(StandardCharsets.UTF_8);

        //数据长度
        int length = msgByte.length;
        out.writeInt(length);

        //消息内容
        out.writeBytes(msgByte);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
