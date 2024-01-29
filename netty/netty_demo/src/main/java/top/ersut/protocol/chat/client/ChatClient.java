package top.ersut.protocol.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.ChatMessageCustomCodecSharable;
import top.ersut.protocol.chat.message.LoginRequestMessage;

import java.net.InetSocketAddress;

@Slf4j
public class ChatClient {

    static final ChatMessageCustomCodecSharable CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER = new ChatMessageCustomCodecSharable();

    //日志处理器
    static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();

        ChannelFuture channelFuture = bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                //LTC解码器，解决半包粘包的问题
                                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                                CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER,
                                LOGGING_HANDLER
                        );
                    }

                })
                .connect(new InetSocketAddress("127.0.0.1", 18808));
        Channel channel = channelFuture.channel();
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                log.info("连接服务端成功");
            } else {
                log.error("连接服务端失败");
            }
        });


        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("关闭客户端失败");
            throw new RuntimeException(e);
        }

    }
}
