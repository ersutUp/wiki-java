package top.ersut.protocol.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.ChatMessageCustomCodecSharable;
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
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                    log.info("msg:{}", msg);
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

                                        System.out.println("等待响应......");
                                        try {
                                            CONSOLE_IN_WAIT.await();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }

                                        if (IS_LOGIN.get()) {
                                            System.out.println("登录成功");
                                        } else {
                                            System.out.println("登录失败");
                                            ctx.channel().close();
                                            return;
                                        }

                                        while (true) {
                                            System.out.println("============ 功能菜单 ============\n"
                                                    + "send [username] [content]\n"
                                                    + "gsend [group name] [content]\n"
                                                    + "gcreate [group name] [m1,m2,m3...]\n"
                                                    + "gmembers [group name]\n"
                                                    + "gjoin [group name]\n"
                                                    + "gquit [group name]\n"
                                                    + "quit\n"
                                                    + "==================================");
                                            //等待用户输入，并阻塞线程
                                            String command = scanner.nextLine();
                                            String[] params = command.split(" ");
                                            switch (params[0]) {
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(params[1],params[2]));
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username,params[1],params[2]));
                                                    break;
                                                case "gcreate":
                                                    //组成员列表
                                                    Set<String> members = Arrays.stream(params[2].split(",")).collect(Collectors.toSet());
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(params[1],members));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(params[1]));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username,params[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username,params[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    //线程结束
                                                    return;
                                            }
                                        }
                                    }, "system in").start();
                                }
                            });

                            ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginResponseMessage>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage loginResponseMessage) throws Exception {
                                    log.info(loginResponseMessage.getReason());
                                    if (loginResponseMessage.isSuccess()) {
                                        IS_LOGIN.set(true);
                                    }
                                    CONSOLE_IN_WAIT.countDown();
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
