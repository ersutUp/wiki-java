package top.ersut.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class EchoDemoTest {

    static final int PORT = 40001;

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                ByteBuf byteBuf = (ByteBuf) msg;
                                String str = byteBuf.readCharSequence(byteBuf.writerIndex(), StandardCharsets.UTF_8).toString();
                                log.info("收到消息:[{}]",str);

                                //回传
                                ByteBuf response = ctx.alloc().buffer();
                                response.writeCharSequence(str,StandardCharsets.UTF_8);
                                ctx.writeAndFlush(response);
                                //变量 response 不需要释放，他会经过一系列出站处理器，由处理器来释放


                                //变量 byteBuf 不需要释放；因为通过 super.channelRead(ctx,msg); 传给了下一个处理器，且下个处理器就是 tail 处理器，他会释放接收到的ByteBuf对象
                                super.channelRead(ctx,msg);
                            }
                        });

                    }
                })
                .bind(PORT);
    }
    static class client{
        public static void main(String[] args) {
            NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
            ChannelFuture channelFuture = new Bootstrap()
                    .group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelOutboundHandlerAdapter(){
                                @Override
                                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                    String string = (String) msg;
                                    //将String转换为ByteBuf
                                    ByteBuf byteBuf = ctx.alloc().buffer();
                                    byteBuf.writeCharSequence(string,StandardCharsets.UTF_8);

                                    super.write(ctx, byteBuf, promise);
                                }
                            });

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    String str = byteBuf.readCharSequence(byteBuf.writerIndex(), StandardCharsets.UTF_8).toString();
                                    log.info("response:[{}]",str);
                                    super.channelRead(ctx,msg);
                                }
                            });
                        }
                    })
                    .connect(new InetSocketAddress(PORT));
            channelFuture.addListener((ChannelFutureListener)(future) -> {
                log.info("客户端启动成功");
                new Thread(() -> {
                    Channel channel = channelFuture.channel();
                    Scanner scanner = new Scanner(System.in);
                    while (true){
                        String text = scanner.nextLine();
                        if(Objects.equals(text,"q") || Objects.equals(text,"quit")){
                            channel.close();
                            break;
                        }
                        channel.write(text);
                        channel.flush();
                    }
                }).start();
            });
            //优雅的关闭客户端
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener)(future) -> {
                eventExecutors.shutdownGracefully();
                log.info("客户端已关闭");
            });
        }
    }


}
