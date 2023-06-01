package top.ersut.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.InetSocketAddress;

@Slf4j
public class HelloNettyTest  {

    static int nettyHelloProt = 16666;

    @Test
    public void nettyServerTest(){
    }

    public static void main(String[] args) {
        //netty的服务端启动器
        new ServerBootstrap()
                //1、配置事件轮询组，NioEventLoopGroup: selector的多线程模式
                .group(new NioEventLoopGroup())
                //2、配置服务端的通道
                .channel(NioServerSocketChannel.class)
                //3、配置与客户端的channel处理器
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    //NioSocketChannel 与客户端的channel
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //4、解码消息，将字节转换为字符串
                        ch.pipeline().addLast(new StringDecoder());
                        //5、入站消息处理器
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            /**
                             * 入站消息的处理
                             * @param ctx channel的上下文
                             * @param msg 入站消息
                             */
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                NioSocketChannel channel = (NioSocketChannel)ctx.channel();
                                InetSocketAddress inetSocketAddress = channel.remoteAddress();
                                //6、打印消息
                                log.info("收到来自[{}]的消息：[{}]",inetSocketAddress.toString(),msg);
                            }
                        });
                    }
                })
                //7、绑定端口
                .bind(nettyHelloProt);
    }

    @SneakyThrows
    @Test
    public void nettyClientTest(){
        //客户端启动器
        new Bootstrap()
                //1、配置事件轮询组，NioEventLoopGroup: 即NIO的selector模式
                .group(new NioEventLoopGroup())
                //2、配置客户端的通道
                .channel(NioSocketChannel.class)
                //3、配置客户端的处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    //初始化客户端
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //4、编码消息，将字符串转换成字节数组
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //5、连接客户端
                .connect(new InetSocketAddress(nettyHelloProt))
                //6、阻塞线程，直到与服务器建立连接
                .sync()
                //7、获取客户端的通道，即NioSocketChannel
                .channel()
                //8、发送消息
                .writeAndFlush("hello netty");
        System.in.read();
    }

}
