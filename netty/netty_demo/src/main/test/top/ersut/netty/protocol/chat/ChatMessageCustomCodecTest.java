package top.ersut.netty.protocol.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;
import top.ersut.protocol.chat.ChatMessageCustomCodec;
import top.ersut.protocol.chat.LoginRequestMessage;

public class ChatMessageCustomCodecTest {

    @Test
    public void encodeTest(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(
                new LoggingHandler(),
                new ChatMessageCustomCodec()
        );

        //encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123", "王");
        embeddedChannel.writeOutbound(loginRequestMessage);
    }

    @Test
    public void decodeTest(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(
                new ChatMessageCustomCodec()
        );

        //要发送的消息
        ByteBuf sendByteBuf = ByteBufAllocator.DEFAULT.buffer();
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123", "王");
        //将 loginRequestMessage 通过 encode 方法写到 sendByteBuf
        new ChatMessageCustomCodec().encode(null,loginRequestMessage,sendByteBuf);

        //decode
        embeddedChannel.writeInbound(sendByteBuf);

    }
    //发生半包的解决方案
    @Test
    public void decodeHalfPackTest(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(
                //todo 添加处理器
                new ChatMessageCustomCodec()
        );

        //要发送的消息
        ByteBuf sendByteBuf = ByteBufAllocator.DEFAULT.buffer();
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123", "王");
        //将 loginRequestMessage 通过 encode 方法写到 sendByteBuf
        new ChatMessageCustomCodec().encode(null,loginRequestMessage,sendByteBuf);

        //todo 模拟半包
        embeddedChannel.writeInbound(sendByteBuf);

    }

}
