package top.ersut.netty.stickhalfpackage;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SolutionByLengthFieldTest {

    public static final int PROT = 30079;

    /**
     * 根据 长度字段 读取内容 具体类{@link LengthFieldBasedFrameDecoder}
     */
    static class LengthFieldServer {
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
                        //长度字段的值是消息内容的长度
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 3, 2));
                        //长度字段的值是整个消息的长度
//                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, -2, 2));
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf byteBuf = (ByteBuf) msg;
                                log.info("byteBuf的容量：[{}]", byteBuf.capacity());
                                log.info("byteBuf的读指针：[{}]，写指针：[{}]", byteBuf.readerIndex(),byteBuf.writerIndex());

                                super.channelRead(ctx, msg);
                            }
                        });
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
    static class LengthFieldClient {
        public static void main(String[] args) {
            NioEventLoopGroup work = new NioEventLoopGroup();
            try {
                ChannelFuture channelFuture = new Bootstrap()
                        .group(work)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                //长度字段 编码器
                                //计算消息长度时不包含长度字段的大小
                                ch.pipeline().addLast(new LengthFieldPrepender(2, -3));
                                //计算消息长度时包含长度字段的大小
//                                ch.pipeline().addLast(new LengthFieldPrepender(2, 2));
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        log.info("发送数据");
                                        for (int i = 0; i < 5; i++) {
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            //自定义的头信息
                                            byte[] header = new byte[]{(byte) 0xff, (byte) 0xaa, (byte) 0xcc};
                                            buffer.writeBytes(header);
                                            //数据
                                            byte[] data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                                            buffer.writeBytes(data);

                                            ctx.writeAndFlush(buffer);
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
