package top.ersut;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SocketChannelTest {

    //阻塞的服务端
    @Test
    public void serverSocketChannelBlockingTest() throws IOException {

        //1、打开服务端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //2、绑定端口
        serverSocketChannel.bind(new InetSocketAddress(6666));
        log.debug("serverSocketChannel start...");

        //用来存放连接的客户端
        List<SocketChannel> clientSocketChannels = new ArrayList<>();

        while (true) {
            //3、与客户端建立连接。并返回一个 SocketChannel 用来与客户端通信；accept()会阻塞
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());
            //新来的客户端放入客户端列表
            clientSocketChannels.add(socketChannel);
            //遍历客户端列表，接收客户端消息
            for (SocketChannel clientSocketChannel : clientSocketChannels) {
                int port = ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getPort();
                ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                //4、获取客户端发来的消息，此处也会阻塞
                clientSocketChannel.read(byteBuffer);
                log.debug("remotePort:[{}],read...", port);
                byteBuffer.flip();

                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                log.debug("remotePort:[{}],message:[{}]", port,charBuffer);

                //清除,准备接收下一次消息
                byteBuffer.clear();
            }
        }
    }

    //非阻塞的服务端
    @Test
    public void serverSocketChannelNonBlockingTest() throws IOException {

        //1、创建服务端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //2、绑定端口
        serverSocketChannel.bind(new InetSocketAddress(6666));
        log.debug("serverSocketChannel start...");

        //设置服务端为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //用来存放连接的客户端
        List<SocketChannel> clientSocketChannels = new ArrayList<>();

        while (true) {
            //3、与客户端建立连接。并返回一个 SocketChannel 用来与客户端通信
            //由于ServerSocketChannel配置了非阻塞模式，此处不会阻塞，没有新客户端时返回null
            SocketChannel socketChannel = serverSocketChannel.accept();

            //判断是否有新客户端
            if(socketChannel != null){
                log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());

                //设置与客户端的连接为非阻塞模式
                socketChannel.configureBlocking(false);

                //新来的客户端放入客户端列表
                clientSocketChannels.add(socketChannel);
            }

            //遍历客户端列表，接收客户端消息
            for (SocketChannel clientSocketChannel : clientSocketChannels) {
                int port = ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getPort();
                ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                //4、获取客户端发来的消息，由于SocketChannel设置了非阻塞此处不再阻塞
                clientSocketChannel.read(byteBuffer);

                //判断是否读取到数据
                if(byteBuffer.position() > 0){
                    log.debug("remotePort:[{}],read...", port);
                    byteBuffer.flip();

                    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                    log.debug("remotePort:[{}],message:[{}]", port,charBuffer);

                    //清除,准备接收下一次消息
                    byteBuffer.clear();
                }
            }
        }
    }

    //客户端
    @Test
    public void socketChannelTest() throws IOException, InterruptedException {
        //打开客户端
        SocketChannel client1 = SocketChannel.open();
        //连接服务端
        client1.connect(new InetSocketAddress("127.0.0.1",6666));
        //发送消息
        client1.write(StandardCharsets.UTF_8.encode("hello!"));
        Thread.sleep(3*1000);
        client1.write(StandardCharsets.UTF_8.encode("hello2!"));

        SocketChannel client2 = SocketChannel.open();
        client2.connect(new InetSocketAddress("127.0.0.1",6666));
        client2.write(StandardCharsets.UTF_8.encode("hi!"));
        Thread.sleep(3*1000);
        client2.write(StandardCharsets.UTF_8.encode("hi2!"));
        Thread.sleep(10*1000);
    }

}
