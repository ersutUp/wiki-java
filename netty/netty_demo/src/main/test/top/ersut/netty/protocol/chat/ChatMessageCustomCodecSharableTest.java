package top.ersut.netty.protocol.chat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import junit.framework.TestCase;
import org.junit.Test;
import top.ersut.protocol.chat.ChatMessageCustomCodec;
import top.ersut.protocol.chat.ChatMessageCustomCodecSharable;
import top.ersut.protocol.chat.message.LoginRequestMessage;

public class ChatMessageCustomCodecSharableTest {


    final ChatMessageCustomCodecSharable CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER = new ChatMessageCustomCodecSharable();

    @Test
    public void testEncode() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(new LoggingHandler(), CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER);

        //encode
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123");
        embeddedChannel.writeOutbound(loginRequestMessage);
    }

    @Test
    public void decodeTest() {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER);

        //要发送的消息
        ByteBuf sendByteBuf = ByteBufAllocator.DEFAULT.buffer();
        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123");
        //将 loginRequestMessage 通过 encode 方法写到 sendByteBuf
        new ChatMessageCustomCodec().encode(null, loginRequestMessage, sendByteBuf);

        //decode
        embeddedChannel.writeInbound(sendByteBuf);

    }
}