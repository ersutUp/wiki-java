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
import top.ersut.protocol.chat.message.LoginResponseMessage;
import top.ersut.protocol.chat.server.service.UserService;
import top.ersut.protocol.chat.server.service.UserServiceFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class ChatClient {

    static final ChatMessageCustomCodecSharable CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER = new ChatMessageCustomCodecSharable();

    //日志处理器
    static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();


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
                                    LOGGING_HANDLER,
                                    CHAT_MESSAGE_CUSTOM_CODEC_SHARABLE_HANDLER
                            );
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.info("msg:{}", msg);
                                    if (msg instanceof LoginResponseMessage) {
                                        LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
                                        log.info(loginResponseMessage.getReason());
                                        ctx.channel().close();
                                    }
                                    super.channelRead(ctx, msg);
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);

                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);
                                        System.out.print("请输入用户名：");
                                        String username = scanner.nextLine();
                                        System.out.print("请输入密码：");
                                        String password = scanner.nextLine();

                                        LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);

                                        ctx.writeAndFlush(loginRequestMessage);

                                    }, "system in").start();
                                }
                            });

                        }

                    })
                    .connect(new InetSocketAddress("127.0.0.1", 18808));
            Channel channel = channelFuture.channel();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("连接服务端成功");
                } else {
                    log.error("连接服务端失败");
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
