package top.ersut.netty.option;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectTimeoutTest {

    public static void main(String[] args) {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            ChannelFuture channelFuture = bootstrap
                    .group(nioEventLoopGroup)
                    /*
                    CONNECT_TIMEOUT_MILLIS参数：连接服务器超过设置的时间，则报错

                    windows：
                        设置为 300 毫秒，当超过 300  毫秒未连接成功，则报错 ConnectTimeoutException（netty的报错）
                        设置为 5000毫秒，当超过 5000 毫秒未连接成功，则报错 ConnectException （java的报错）
                    Mac：
                        未复现 ConnectTimeoutException 错误；
                        只报错 ConnectException（应该是与windows底层逻辑不一样）。
                     */
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler())
                    .connect("localhost", 888);

            channelFuture.sync()
                    .channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("err",e);
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }


    }

}
