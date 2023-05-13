package top.ersut;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
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


    //Selector的 accept和Read 事件的处理
    @Test
    public void SelectorAcceptAndReadTest() {
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
                        //注册 read 事件
                        socketChannel.register(selector,SelectionKey.OP_READ);
                    } else if(selectionKey.isReadable()){
                        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                        int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

                        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
                        try {
                            int len = socketChannel.read(byteBuffer);
                            //-1代表客户端关闭
                            if(len == -1){
                                log.debug("remotePort:[{}],close...",port);
                                //退出Selector
                                selectionKey.cancel();
                                socketChannel.close();
                                continue;
                            }
                        } catch (IOException e){
                            //处理客户端强制关闭的情况
                            log.debug("remotePort:[{}],error...",port);
                            //退出Selector
                            selectionKey.cancel();
                            socketChannel.close();
                            continue;
                        }
                        log.debug("remotePort:[{}],read...",port);

                        byteBuffer.flip();
                        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
                        log.debug("remotePort:[{}],message:[{}]", port,charBuffer);
                        byteBuffer.clear();
                    }
                }
                //事件处理后移除，否则该事件还会进入下一轮循环
                iterator.remove();
            }
        } catch (IOException e){
            log.error("",e);
        }

    }

    /**
     * 处理消息边界
     * 每条消息以\n结尾
     */
    @Test
    public void messageBoundaryTest() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             //创建 Selector
             Selector selector = Selector.open();) {

            serverSocketChannel.bind(new InetSocketAddress(6667));
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
                    if(selectionKey.isAcceptable()){
                        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel socketChannel = channel.accept();

                        socketChannel.configureBlocking(false);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
                        //注册 read 事件 , 添加第三个参数：注册一个SelectionKey级的变量
                        socketChannel.register(selector,SelectionKey.OP_READ,byteBuffer);
                    } else if(selectionKey.isReadable()){
                        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                        int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();

                        //获取在SelectionKey中存入的变量
                        ByteBuffer byteBuffer = (ByteBuffer)selectionKey.attachment();
                        try {
                            int len = socketChannel.read(byteBuffer);
                            log.debug("remotePort:[{}],read...",port);
                            //-1代表客户端关闭
                            if(len == -1){
                                log.debug("remotePort:[{}],close...",port);
                                //退出Selector
                                selectionKey.cancel();
                                socketChannel.close();
                                continue;
                            }
                        } catch (IOException e){
                            //处理客户端强制关闭的情况
                            log.debug("remotePort:[{}],error...",port);
                            //退出Selector
                            selectionKey.cancel();
                            socketChannel.close();
                            continue;
                        }
                        byte[] message;
                        //循环，处理一次读取中多条信息的情况。
                        do {
                            message = split(byteBuffer);
                            //是否需要扩容
                            if(byteBuffer.position() == byteBuffer.limit()){
                                byteBuffer.flip();
                                //没有分割符 扩容
                                int size = byteBuffer.capacity() * 2;
                                log.debug("remotePort:[{}],dilatation:[{}]", port,size);
                                ByteBuffer newByteBuffer = ByteBuffer.allocate(size);
                                newByteBuffer.put(byteBuffer);
                                selectionKey.attach(newByteBuffer);
                                break;
                            }
                            //判断是否有消息
                            if (message != null){
                                String str = new String(message, StandardCharsets.UTF_8);
                                log.debug("remotePort:[{}],message:[{}]", port,str);
                            }
                        }while (message != null);
                    }
                }
                //事件处理后移除，否则该事件还会进入下一轮循环
                iterator.remove();
            }
        } catch (IOException e){
            log.error("",e);
        }
    }

    /**
     * 解析数据
     * @param souce
     */
    private byte[] split(ByteBuffer souce){
        byte[] msg = null;
        //改为读模式，并获取limit
        int len = souce.flip().limit();
        for (int i = 0; i < len; i++) {
            byte currByte = souce.get(i);
            //如果有\n 代表有消息；
            if(currByte == '\n'){
                msg = new byte[i+1];
                for (int j = 0; j <= i; j++) {
                    msg[j] = souce.get();
                }
                break;
            }
        }
        //如果没有分割符，那么执行compact后，position和limit相等
        souce.compact();
        return msg;
    }

    //消息边界的客户端
    @Test
    public void messageBoundaryClientTest(){
        try (SocketChannel socketChannel = SocketChannel.open();){
            socketChannel.connect(new InetSocketAddress(6667));
            socketChannel.write(StandardCharsets.UTF_8.encode("1949年，"));
            socketChannel.write(StandardCharsets.UTF_8.encode("中华人民共和国\n"));
            socketChannel.write(StandardCharsets.UTF_8.encode("成立啦！\n牛逼!\n"));
            Thread.sleep(3*1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
