package top.ersut.netty.protocol.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;
import top.ersut.protocol.chat.ChatMessageCustomCodec;
import top.ersut.protocol.chat.message.LoginRequestMessage;

public class ChatMessageCustomCodecTest {

    @Test
    public void encodeTest(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(
                new LoggingHandler(),
                new ChatMessageCustomCodec()
        );

        //encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123");
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
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123");
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
                /** 添加 LengthFieldBasedFrameDecoder 解码器，解决半包粘包问题 */
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new ChatMessageCustomCodec()
        );

        //要发送的消息
        ByteBuf sendByteBuf = ByteBufAllocator.DEFAULT.buffer();
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123");
        //将 loginRequestMessage 通过 encode 方法写到 sendByteBuf
        new ChatMessageCustomCodec().encode(null,loginRequestMessage,sendByteBuf);

        /** 模拟半包 */
        int firstIndex = 6;
        ByteBuf byteBuf1 = sendByteBuf.readSlice(firstIndex);
        //因为分成了两个包并发送两次 每次发送都会执行release 所以这里retain一次
        sendByteBuf.retain();
        embeddedChannel.writeInbound(byteBuf1);

        ByteBuf byteBuf2 = sendByteBuf.readSlice(sendByteBuf.writerIndex() - firstIndex);
        embeddedChannel.writeInbound(byteBuf2);

    }

}
