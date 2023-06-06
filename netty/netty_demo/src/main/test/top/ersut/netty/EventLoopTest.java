package top.ersut.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EventLoopTest {

    private static final int PORT = 14581;

    @Test
    public void eventLoopGroupTest(){
        // EventLoopGroup 事件循环组
        // EventLoopGroup 内有多个 EventLoop
        /**
         * DefaultEventLoopGroup 处理 普通任务和定时任务
         * 无参构造：默认一个线程
         * 有参构造： nThreads 指定线程数
         *
         * 一个线程对应一个EventLoop
         */
        EventLoopGroup group = new DefaultEventLoopGroup(2);

        // NioEventLoopGroup 处理 IO事件、普通任务和定时任务
        //EventLoopGroup group = new NioEventLoopGroup();

        //第一个eventLoop
        EventLoop eventLoop1 = group.next();
        //第二个eventLoop
        EventLoop eventLoop2 = group.next();
        //第一个eventLoop
        EventLoop eventLoop3 = group.next();
        //第二个eventLoop
        EventLoop eventLoop4 = group.next();

        //由于只有两个线程所以只有两个eventLoop，那么第一次和第三次获取的是同一个eventLoop，同理第二次和第四次获取的是同一个eventLoop
        Assert.assertEquals(eventLoop1,eventLoop3);
        Assert.assertEquals(eventLoop2,eventLoop4);

        //添加任务
        group.next().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("给eventLoop添加任务");
        });

        //添加定时任务
        group.next().scheduleAtFixedRate(
                () -> log.debug(String.valueOf(LocalDateTime.now().getSecond()))
                //多少秒后执行第一次
                ,0
                //每隔多久执行一次
                ,1
                //参数3的时间单位
                , TimeUnit.SECONDS
        );


        log.debug("主线程");

        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 优雅的关闭 EventLoopGroup ，拒绝添加新任务，然后将当前的任务执行完后停止EventLoop对应的线程
        group.shutdownGracefully();
    }

    //两个客户端各发送两条消息
    @Test
    public void client() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(PORT))
                .sync()
                .channel();
        Channel channel2 = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(PORT))
                .sync()
                .channel();
        channel.writeAndFlush("123");
        Thread.sleep(1000);
        channel2.writeAndFlush("abc");
        Thread.sleep(1000);
        channel.writeAndFlush("456");
        Thread.sleep(1000);
        channel2.writeAndFlush("def");

        group.shutdownGracefully().sync();
        channel.close();
        channel2.close();

    }

    /**
     * 多个group，分工不同
     */
    public static void groupServer(){
        new ServerBootstrap()
                /**
                 * 参数1 parentGroup：处理 NioServerSocketChannel 的accept
                 * 参数2 childGroup：处理 NioSocketChannel 的 读写
                 */
                .group(new NioEventLoopGroup(1),new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf message = (ByteBuf) msg;
                                log.debug(message.toString(StandardCharsets.UTF_8));
                            }
                        });
                    }
                })
                .bind(PORT);
    }

    /**
     * 给某个处理器指定 EventLoopGroup
     * 适用场景：执行时间长的处理器
     * 当某个处理器处理时间长，给该处理器指定 EventLoopGroup。否则会导致读写的 EventLoopGroup 吞吐量降低，因为默认是在读写的 EventLoopGroup 中执行处理器
     */
    public static void pipelineAppointGroupServer(){
        EventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(2);
        new ServerBootstrap()
                /**
                 * 参数1 parentGroup：处理 NioServerSocketChannel 的accept
                 * 参数2 childGroup：处理 NioSocketChannel 的 读写
                 */
                .group(new NioEventLoopGroup(),new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf message = (ByteBuf) msg;
                                log.debug(message.toString(StandardCharsets.UTF_8));
                                //传给pipeline中的下一个处理器
                                super.channelRead(ctx,msg);
                            }
                        });
                        ch.pipeline().addLast(defaultEventLoopGroup,"default",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf message = (ByteBuf) msg;
                                log.debug(message.toString(StandardCharsets.UTF_8)+"-default");
                            }
                        });
                    }
                })
                .bind(PORT);
    }

    public static void main(String[] args) {
        //分工不同的group
//        groupServer();

        //给某个处理器指定 EventLoopGroup
        pipelineAppointGroupServer();

    }


}
