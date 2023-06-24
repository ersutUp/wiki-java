package top.ersut.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class InboundAndOutboundTest {

    private final static int PORT = 48867;
    private final static int PORT2 = 48167;

    private Channel client(int port) throws InterruptedException {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //处理器的顺序
                //有俩个默认处理器（head、tail），head在最前，tail在尾部，他两个的位置不受新处理器的影响
                //以下处理器的存储结构（双向链表） head <---> inbound-1 <---> inbound-2 <---> outbound-1 <---> inbound-3 <---> outbound-2 <---> outbound-3 <---> tail
                //注意：处理入站消息时从 head 处理器开始找，最终执行顺序 inbound-1 -> inbound-2 -> inbound-3
                //     处理出战消息时从 tail 处理器开始找，最终执行顺序 outbound-3 -> outbound-2 -> outbound-1
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast("inbound-1",new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("客户端收到消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                                        log.info("第一个自定义入站处理器:{}",ctx.name());
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast("inbound-2",new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("第二个自定义入站处理器:{}",ctx.name());
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast("outbound-1",new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.info("客户端发送消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                                        log.info("第一个自定义出站处理器:{}",ctx.name());
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast("inbound-3",new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("第三个自定义入站处理器:{}",ctx.name());
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast("outbound-2",new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.info("第二个自定义出站处理器:{}",ctx.name());
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast("outbound-3",new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.info("第三个自定义出站处理器:{}",ctx.name());
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                })
                .connect(new InetSocketAddress(port))
                .sync()
                .channel();
        return channel;
    }

    //测试入站处理器
    @Test
    public void inboundTest() throws InterruptedException {
        Channel channel = client(PORT);

        Thread.sleep(1000);

        channel.close().sync();
    }

    //测试出站处理器
    @Test
    public void outboundTest() throws InterruptedException {
        Channel channel = client(PORT2);

        channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("这是客户端发来的消息".getBytes(StandardCharsets.UTF_8)))
                .sync();

        Thread.sleep(1000);

        channel.close().sync();
    }

    //测试入站以及出站处理器
    @Test
    public void inboundAndOutboundTest() throws InterruptedException {
        Channel channel = client(PORT);

        channel.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("这是客户端发来的消息".getBytes(StandardCharsets.UTF_8)))
                .sync();

        Thread.sleep(1000);

        channel.close().sync();
    }

    @Test
    public void inboundByEmbeddedChannelTest() {
        // EmbeddedChannel 是一个用于测试 ChannelHandler 的类
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline()
                .addLast("inbound-1",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("客户端收到消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                        log.info("第一个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);
                    }
                })
                .addLast("inbound-2",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("第二个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);
                    }
                })
                .addLast("inbound-3",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("第三个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);
                    }
                });
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("embeddedChannel-inbound".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void outboundByEmbeddedChannelTest() {
        // EmbeddedChannel 是一个用于测试 ChannelHandler 的类
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline()
                .addLast("outbound-1",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("客户端发送消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                        log.info("第一个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                })
                .addLast("outbound-2",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("第二个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                })
                .addLast("outbound-3",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("第三个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                });
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("embeddedChannel-outbound".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void inboundAndOutboundByEmbeddedChannelTest() {
        // EmbeddedChannel 是一个用于测试 ChannelHandler 的类
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline()
                .addLast("inbound-1",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("客户端收到消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                        log.info("第一个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);
                    }
                })
                .addLast("inbound-2",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("第二个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);
                    }
                })
                .addLast("outbound-1",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("客户端发送消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                        log.info("第一个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                })
                .addLast("inbound-3",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("第三个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);
                    }
                })
                .addLast("outbound-2",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("第二个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                })
                .addLast("outbound-3",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("第三个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                });
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("embeddedChannel-Inbound".getBytes(StandardCharsets.UTF_8)));
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("embeddedChannel-outbound".getBytes(StandardCharsets.UTF_8)));

        ByteBuf byteBuf = embeddedChannel.readInbound();
        log.info("msg:{}",byteBuf.toString(StandardCharsets.UTF_8));
    }



    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class);


        //该服务端在channel激活成功会发送一条消息给客户端（测试客户端入站的服务端）
        serverBootstrap
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.channel().writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes("这是条服务端发来的消息".getBytes(StandardCharsets.UTF_8)));
                                super.channelActive(ctx);
                            }
                        });
                    }
                })
                .bind(PORT);

        //不会发送消息的服务端
        serverBootstrap
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                })
                .bind(PORT2);
    }

}
