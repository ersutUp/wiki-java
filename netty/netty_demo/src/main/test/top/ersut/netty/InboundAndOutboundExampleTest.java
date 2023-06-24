package top.ersut.netty;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class InboundAndOutboundExampleTest {

    private final static int PORT = 18867;

    @Data
    @Builder
    private static class StudentInfo{
        private String name;
        private int age;
        private String sex;
    }

    @Test
    public void clientTest() throws InterruptedException {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast("outbound-3",new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        String base64 = (String) msg;
                                        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer().writeBytes(base64.getBytes(StandardCharsets.UTF_8));
                                        super.write(ctx, byteBuf , promise);
                                    }
                                })
                                .addLast("outbound-2",new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        String str = (String) msg;
                                        String encode = Base64.encode(str);
                                        log.info("student base64:{}",encode);
                                        super.write(ctx, encode, promise);
                                    }
                                })
                                .addLast("outbound-1",new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        StudentInfo studentInfo = (StudentInfo) msg;
                                        String jsonStr = JSONUtil.toJsonStr(studentInfo);
                                        log.info("student json:{}",jsonStr);
                                        super.write(ctx, jsonStr, promise);
                                    }
                                });
                    }
                })
                .connect(new InetSocketAddress(PORT))
                .sync()
                .channel();


        StudentInfo studentInfo = StudentInfo
                .builder()
                .name("张三")
                .sex("男")
                .age(18)
                .build();
        log.info("studentInfo:{}",studentInfo);
        channel.writeAndFlush(studentInfo).sync();

        channel.close().sync();
    }



    public static void main(String[] args) {
        new ServerBootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast("inbound-1",new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf byteBuf = (ByteBuf) msg;
                                    log.info("msg byteBuf:{}",byteBuf);
                                    String base64 = byteBuf.toString(StandardCharsets.UTF_8);
                                    super.channelRead(ctx, base64);
                                }
                            })
                            .addLast("inbound-2",new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    String base64 = (String) msg;
                                    log.info("msg base64:{}",base64);
                                    String json = Base64.decodeStr(base64);
                                    super.channelRead(ctx, json);
                                }
                            })
                            .addLast("inbound-3",new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    String json = (String) msg;
                                    log.info("msg json:{}",json);
                                    StudentInfo studentInfo = JSONUtil.toBean(json, StudentInfo.class);
                                    log.info("studentInfo:{}",studentInfo);
                                    super.channelRead(ctx, studentInfo);
                                }
                            });
                }
            })
            .bind(PORT);
    }
}
