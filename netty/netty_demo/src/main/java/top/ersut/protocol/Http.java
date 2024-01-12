package top.ersut.protocol;

import cn.hutool.http.ContentType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Http {

    private static final int PROT = 8080;
    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup works = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,works);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
//                    ch.pipeline().addLast(new LoggingHandler());
                    //http的编解码处理器
                    ch.pipeline().addLast(new HttpServerCodec());
                    //请求内容部分
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpContent>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
                            log.info(msg.content().toString(StandardCharsets.UTF_8));
                        }
                    });
                    //SimpleChannelInboundHandler:接受一个泛型，只处理匹配泛型的msg
                    //请求信息部分（请求头、协议版本、等）
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                            //获取请求路径
                            String uri = msg.uri();
                            log.info("请求路径[{}]",uri);
                            //http的版本
                            HttpVersion httpVersion = msg.protocolVersion();

                            //创建响应
                            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(httpVersion,HttpResponseStatus.OK);

                            String content = "<h1>请求路径："+uri+"</h1>";
                            byte[] httpContent = content.getBytes(StandardCharsets.UTF_8);

                            //响应的Http内容
                            httpResponse.content().writeBytes(httpContent);
                            //设置内容的长度，以便客户端解析
                            httpResponse.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,httpContent.length);
                            //设置编码
                            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, ContentType.TEXT_HTML+";charset="+StandardCharsets.UTF_8);

                            //写出数据
                            ctx.writeAndFlush(httpResponse);
                        }
                    });
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(PROT);

            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    log.info("服务端启动成功");
                } else {
                    log.info("服务端启动失败");
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
