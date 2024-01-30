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
import top.ersut.protocol.chat.message.LoginRequestMessage;
import top.ersut.protocol.chat.message.LoginResponseMessage;
import top.ersut.protocol.chat.server.service.UserService;
import top.ersut.protocol.chat.server.service.UserServiceFactory;
import top.ersut.protocol.chat.server.session.Group;

import java.util.Scanner;


@Slf4j
public class ChatServer {

    static final ChatMessageCustomCodecSharable CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER = new ChatMessageCustomCodecSharable();

    //日志处理器
    static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

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
                            ch.pipeline().addLast(
                                    new SimpleChannelInboundHandler<LoginRequestMessage>() {

                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                                            LoginResponseMessage loginResponseMessage;

                                            UserService userService = UserServiceFactory.getUserService();
                                            if (userService.login(msg.getAccount(), msg.getPassword())) {
                                                loginResponseMessage = new LoginResponseMessage(true, "登录成功");
                                            } else {
                                                loginResponseMessage = new LoginResponseMessage(false, "登录成功");
                                            }

                                            ctx.writeAndFlush(loginResponseMessage);
                                        }
                                    }
                            );
                        }
                    }).bind(18808);

            Channel channel = bindFuture.channel();
            bindFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("服务端启动成功");
                } else {
                    log.error("服务端启动失败");
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
