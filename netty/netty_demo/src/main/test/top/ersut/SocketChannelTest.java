package top.ersut;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

        //3、设置服务端为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //用来存放连接的客户端
        List<SocketChannel> clientSocketChannels = new ArrayList<>();

        while (true) {
            //4、与客户端建立连接。并返回一个 SocketChannel 用来与客户端通信
            //由于ServerSocketChannel配置了非阻塞模式，此处不会阻塞，没有新客户端时返回null
            SocketChannel socketChannel = serverSocketChannel.accept();

            //判断是否有新客户端
            if(socketChannel != null){
                log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());

                //5、设置与客户端的连接为非阻塞模式
                socketChannel.configureBlocking(false);

                //新来的客户端放入客户端列表
                clientSocketChannels.add(socketChannel);
            }

            //遍历客户端列表，接收客户端消息
            for (SocketChannel clientSocketChannel : clientSocketChannels) {
                int port = ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getPort();
                ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                //6、获取客户端发来的消息，由于SocketChannel设置了非阻塞此处不再阻塞，没有消息时byteBuffer中不存在有效数据
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


    //非阻塞的服务端
    @Test
    public void Test() throws IOException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             //创建 Selector
             Selector selector = Selector.open();) {

            serverSocketChannel.bind(new InetSocketAddress(6666));
            log.debug("serverSocketChannel start...");

            //只有非阻塞的channel才能使用Selector
            serverSocketChannel.configureBlocking(false);
            //服务端中注册selector，并监听 accept 事件
            //register方法的参数1 Selector；参数2 监听的事件类型
            SelectionKey selectionKeyByServer = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                //等待触发监听的事件，这里会阻塞
                int count = selector.select();

                //获取监听到的事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                //遍历所有事件
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    log.debug("selectionKey:[{}]",selectionKey);
                    if(selectionKey.isAcceptable()){
                        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel socketChannel = channel.accept();
                        log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());

                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    } else if(selectionKey.isReadable()){
                        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                        int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                        socketChannel.read(byteBuffer);
                        log.debug("remotePort:[{}],read...",port);

                        byteBuffer.flip();
                        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                        log.debug("remotePort:[{}],message:[{}]", port,charBuffer);

                    }
                }
                iterator.remove();

//
//                //判断是否有新客户端
//                if(socketChannel != null){
//                    log.debug("remotePort:[{}],connected...",((InetSocketAddress)socketChannel.getRemoteAddress()).getPort());
//
//                    //5、设置与客户端的连接为非阻塞模式
//                    socketChannel.configureBlocking(false);
//
//                    //新来的客户端放入客户端列表
//                    clientSocketChannels.add(socketChannel);
//                }
//
//                //遍历客户端列表，接收客户端消息
//                for (SocketChannel clientSocketChannel : clientSocketChannels) {
//                    int port = ((InetSocketAddress) clientSocketChannel.getRemoteAddress()).getPort();
//                    ByteBuffer byteBuffer = ByteBuffer.allocate(16);
//                    //6、获取客户端发来的消息，由于SocketChannel设置了非阻塞此处不再阻塞，没有消息时byteBuffer中不存在有效数据
//                    clientSocketChannel.read(byteBuffer);
//
//                    //判断是否读取到数据
//                    if(byteBuffer.position() > 0){
//                        log.debug("remotePort:[{}],read...", port);
//                        byteBuffer.flip();
//
//                        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
//                        log.debug("remotePort:[{}],message:[{}]", port,charBuffer);
//
//                        //清除,准备接收下一次消息
//                        byteBuffer.clear();
//                    }
//                }
            }
        }

    }

}
