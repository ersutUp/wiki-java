package top.ersut.protocol.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.ChatMessageCustomCodecSharable;
import top.ersut.protocol.chat.client.handler.ChatHandler;
import top.ersut.protocol.chat.client.handler.HeartbeatHandlerFactory;
import top.ersut.protocol.chat.client.handler.LoginResponseMessageHandler;
import top.ersut.protocol.chat.message.*;
import top.ersut.protocol.chat.server.service.UserService;
import top.ersut.protocol.chat.server.service.UserServiceFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class ChatClient {

    static final ChatMessageCustomCodecSharable CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER = new ChatMessageCustomCodecSharable();

    //日志处理器
    static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        //用户在控制台输入等待
        CountDownLatch CONSOLE_IN_WAIT = new CountDownLatch(1);
        //是否登录
        AtomicBoolean IS_LOGIN = new AtomicBoolean(false);
        //是否退出
        AtomicBoolean IS_EXIT = new AtomicBoolean(false);

        HeartbeatHandlerFactory heartbeatHandlerFactory = new HeartbeatHandlerFactory();

        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture channelFuture = bootstrap
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {

                            ch.pipeline().addLast(
                                    //LTC解码器，解决半包粘包的问题
                                    new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
//                                    LOGGING_HANDLER,
                                    CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER
                            );
                            //心跳部分
                            ch.pipeline().addLast(
                                    heartbeatHandlerFactory.getIdleStateHandler(),
                                    heartbeatHandlerFactory.getPingMessageHandler()
                            );

                            //客户端的消息处理器
                            ch.pipeline().addLast(new ChatHandler(CONSOLE_IN_WAIT,IS_LOGIN,IS_EXIT));
                            //客户端登录响应处理器
                            ch.pipeline().addLast(new LoginResponseMessageHandler(IS_LOGIN,CONSOLE_IN_WAIT));
                        }

                    })
                    .connect(new InetSocketAddress("127.0.0.1", 18808));
            Channel channel = channelFuture.channel();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("连接服务端成功");
                } else {
                    log.error("连接服务端失败",future.cause());
                    channel.close();
                }
            });

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("关闭客户端失败");
            throw new RuntimeException(e);
        } finally {
            if (!eventLoopGroup.isShuttingDown()) {
                eventLoopGroup.shutdownGracefully();
            }
        }

    }
}
