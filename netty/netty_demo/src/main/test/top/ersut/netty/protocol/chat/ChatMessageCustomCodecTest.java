package top.ersut.netty.protocol.chat;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;
import top.ersut.protocol.chat.ChatMessageCustomCodec;
import top.ersut.protocol.chat.LoginRequestMessage;

public class ChatMessageCustomCodecTest {

    @Test
    public void out(){
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(
                new LoggingHandler(),
                new ChatMessageCustomCodec()
        );

        LoginRequestMessage loginRequestMessage = new LoginRequestMessage("ersut", "123", "王");
        embeddedChannel.writeOutbound(loginRequestMessage);
    }

}
