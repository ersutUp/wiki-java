package top.ersut.protocol.chat.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.ChatMessageCustomCodecSharable;
import top.ersut.protocol.chat.server.handler.ChatRequestMessageHandler;
import top.ersut.protocol.chat.server.handler.LoginRequestMessageHandler;


@Slf4j
public class ChatServer {

    static final ChatMessageCustomCodecSharable CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER = new ChatMessageCustomCodecSharable();

    //日志处理器
    static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
    static final LoginRequestMessageHandler LOGIN_REQUEST_MESSAGE_HANDLER = new LoginRequestMessageHandler();
    static final ChatRequestMessageHandler CHAT_REQUEST_MESSAGE_HANDLER = new ChatRequestMessageHandler();

    public static void main(String[] args) {

                            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup works = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            try {
                ChannelFuture bindFuture = serverBootstrap
                        .group(boss, works)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    //LTC解码器，解决半包粘包的问题
                                    new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                                    LOGGING_HANDLER,
                                    CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER

                            );
                            ch.pipeline().addLast(LOGIN_REQUEST_MESSAGE_HANDLER);
                            ch.pipeline().addLast(CHAT_REQUEST_MESSAGE_HANDLER);
                        }
                    }).bind(18808);

            Channel channel = bindFuture.channel();
            bindFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("服务端启动成功");
                } else {
                    log.error("服务端启动失败",future.cause());
                }
            });

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("关闭服务端失败");
            throw new RuntimeException(e);
        } finally {
            if (!boss.isShuttingDown()) {
                boss.shutdownGracefully();
            }
            if (!works.isShuttingDown()) {
                works.shutdownGracefully();
            }
        }

    }

}
