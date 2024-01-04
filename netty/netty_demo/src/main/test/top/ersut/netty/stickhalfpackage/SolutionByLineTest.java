package top.ersut.netty.stickhalfpackage;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class SolutionByLineTest {

    public static final int PROT = 30078;

    /**
     * 根据 定界符：换行(\n or \r\n) 解决粘包半包问题
     * 自定义 定界符 查看{@link DelimiterBasedFrameDecoder}
     */
    static class LinePackageServer {
        public static void main(String[] args) {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup works = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(boss, works);
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //根据 换行符 进行解码
                        ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                });
                ChannelFuture channelFuture = serverBootstrap.bind(PROT);

                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("启动成功");
                    } else {
                        log.error("启动失败", future.cause());
                    }
                });
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                boss.shutdownGracefully();
                works.shutdownGracefully();
            }
        }
    }

    /**
     * 客户端
     */
    static class LinePackageClient {
        public static void main(String[] args) {
            NioEventLoopGroup work = new NioEventLoopGroup();
            try {
                ChannelFuture channelFuture = new Bootstrap()
                        .group(work)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                //在每条消息最后添加 换行符 的编码器
                                ch.pipeline().addLast(new LineEncoder());
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        log.info("发送数据");
                                        for (int i = 0; i < 5; i++) {
                                            //这里要写入 CharSequence类型， 因为LineEncoder只处理 CharSequence类型 的数据
                                            ctx.writeAndFlush(new String(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
                                        }
                                        ctx.channel().close();
                                    }
                                });
                            }
                        })
                        .connect(new InetSocketAddress(PROT));
                Channel channel = channelFuture.channel();

                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("启动成功");
                    } else {
                        log.error("启动失败", future.cause());
                    }
                });
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                work.shutdownGracefully();
            }
        }
    }

}
