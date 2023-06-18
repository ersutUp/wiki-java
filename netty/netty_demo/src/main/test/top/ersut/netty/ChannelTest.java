package top.ersut.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
public class ChannelTest {


    private static final int PORT = 15218;

    public static Bootstrap bootstrap(){
        return bootstrap(new NioEventLoopGroup());
    }
    public static Bootstrap bootstrap(NioEventLoopGroup nioEventLoopGroup){
        return new Bootstrap()
                .group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //打印详细日志
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });
    }

    /**
     * 以下代码存在的问题：过早的获取channel，导致消息发送失败
     */
    static class PrematureGetChannelTest{
        public static void main(String[] args) throws InterruptedException {
            ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

            Channel channel = channelFuture.channel();
            channel.write("not sync");
            log.info("channel isActive:{}",channel.isActive());
        }
    }

    /**
     * 通过 ChannelFuture#sync() 解决 过早的获取channel 的问题
     * ChannelFuture#sync()方法:等待异步线程完成，此处是等待连接成功
     */
    static class ChannelBySyncTest{
        public static void main(String[] args) throws InterruptedException {
            ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

            //等待异步线程的结果
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush("sync");
            log.info("channel isActive:{}",channel.isActive());
        }
    }
    /**
     * channelFuture.addListener(ChannelFutureListener listener)方法:异步线程中连接成功后执行发送消息
     * 通过给 ChannelFuture 添加监听器 解决 过早的获取channel 的问题
     */
    static class ChannelByListenerTest{
        public static void main(String[] args) {
            ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

            //等待连接成功
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                Channel channel = channelFuture1.channel();
                channel.writeAndFlush("ChannelFutureListener");
                log.info("channel isActive:{}",channel.isActive());
            });
        }
    }

    /**
     * 关闭客户端
     * 以下代码存在的问题：
     *  1、由于客户端关闭是在NioEventLoop线程中进行的，所以主线程中不能正确的执行关闭后的任务
     *  2、关闭Channel后进程没有终止
     */
    static class ChannelCloseTest {
        public static void main(String[] args) throws InterruptedException {
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));

            //等待连接成功
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush("channelClose");
            log.info("close......");
            channel.close();
            //期望channel成功关闭后再运行
            log.info("closed");
        }
    }
    /**
     * 问题1的解决方案A，将主线程改为同步阻塞的等待channl的关闭
     */
    static class ChannelCloseFutureSyncTest{
        public static void main(String[] args) throws InterruptedException {
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));

            //等待连接成功
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            new Thread(() -> {
                channel.writeAndFlush("channelCloseFutureSync");
                log.info("close......");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).run();
            //eventLoopGroup中还有线程，关闭eventLoopGroup中的线程，同时会关闭channel
            eventLoopGroup.shutdownGracefully().sync();
            log.info("closed");
        }
    }
    /**
     * 1、解决方案B：正确处理关闭客户端后的任务，通过调用closeFuture再添加监听器解决
     * <code>
     *     ChannelFuture closeFuture = channel.closeFuture();
     *     closeFuture.addListener((ChannelFutureListener) future -> {
     *         log.info("closed");
     *     });
     * </code>
     * 2、关闭 eventLoopGroup 后可结束进程，通过 shutdownGracefully() 即可关闭 eventLoopGroup
     */
    static class ChannelCloseFutureListenerTest{
        public static void main(String[] args) throws InterruptedException {
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));

            //等待连接成功
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush("channelCloseFutureListener");
            log.info("channel isActive:{}",channel.isActive());

            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.addListener((ChannelFutureListener) future -> {
                Channel channel1 = future.channel();
                log.info("channel isActive:{}",channel1.isActive());
                log.info("closed");
            });

            log.info("close......");
            //eventLoopGroup中还有线程，关闭eventLoopGroup中的线程，同时会关闭channel
           eventLoopGroup.shutdownGracefully();
        }
    }



    public static void main(String[] args) {
        log.info("启动服务端");
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                String message = (String) msg;
                                log.info(message);
                            }
                        });
                    }
                })
                .bind(PORT);
        channelFuture.addListener((ChannelFutureListener) (future) -> {
            log.info("服务端启动完成");
        });
        Channel serverChannel = channelFuture.channel();
        serverChannel.closeFuture().addListener(future -> {

        });
        serverChannel.close();
    }

}
