package top.ersut.netty.stickhalfpackage;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class StickHalfPackageTest {

    public static final int PROT = 30076;

    /**
     * 滑动窗口复现粘包和半包服务端（mac不好复现半包）<br/>
     * 通过 {@link ChannelOption#SO_RCVBUF} 设置滑动窗口大小
     */
     static class StickPackageServer{
        public static void main(String[] args) {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup works = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(boss, works);
                // 设置服务器端接收缓冲区大小为 10 字节
                serverBootstrap.option(ChannelOption.SO_RCVBUF, 10);
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                });
                ChannelFuture channelFuture = serverBootstrap.bind(PROT);

                channelFuture.addListener(future->{
                    if (future.isSuccess()) {
                        log.info("启动成功");
                    } else {
                        log.error("启动失败",future.cause());
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
     * 应用层复现半包和粘包服务端<br/>
     * 通过 {@link ChannelOption#RCVBUF_ALLOCATOR} 设置NioSocketChannel中ByteBuf的大小
     */
    static class HalfPackageServer{
        public static void main(String[] args) {
            NioEventLoopGroup boos = new NioEventLoopGroup();
            NioEventLoopGroup works = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                        serverBootstrap.channel(NioServerSocketChannel.class)
//                        .option(ChannelOption.SO_RCVBUF,10)
                        //NioSocketChannel 中ByteBuf 大小限制 16个字节
                        .childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))
                        .group(boos, works)
                        .childHandler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
//                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
//                                    @Override
//                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                        ByteBuf byteBuf = ((ByteBuf) msg);
//                                        log.info("byteBuf.writerIndex():[{}]",byteBuf.writerIndex());
//                                        log.info("byteBuf.capacity():[{}]",byteBuf.capacity());
//                                        super.channelRead(ctx, msg);
//                                    }
//                                });
                                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                            }
                        });
                ChannelFuture channelFuture = serverBootstrap.bind(PROT);
                channelFuture.addListener(future->{
                    if (future.isSuccess()) {
                        log.info("启动成功");
                    } else {
                        log.error("启动失败",future.cause());
                    }
                });
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                boos.shutdownGracefully();
                works.shutdownGracefully();
            }
        }
    }

    /**
     * 客户端
     */
    static class HalfPackageClient{
        public static void main(String[] args) {
            NioEventLoopGroup work = new NioEventLoopGroup();
            try {
                ChannelFuture channelFuture = new Bootstrap()
                        .group(work)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        log.info("发送数据");
                                        for (int i = 0; i < 5; i++) {
                                            ByteBuf byteBuf = ctx.alloc().buffer();
                                            //每条信息10个字节
                                            byteBuf.writeBytes(new byte[]{0,1, 2, 3, 4, 5, 6, 7,8,9});
                                            ctx.writeAndFlush(byteBuf);
                                        }
                                        ctx.channel().close();
                                    }
                                });
//                                ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            }
                        })
                        .connect(new InetSocketAddress(PROT));
                Channel channel = channelFuture.channel();

                channelFuture.addListener(future -> {
                    if (future.isSuccess()) {
                        log.info("启动成功");
                    } else {
                        log.error("启动失败",future.cause());
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
