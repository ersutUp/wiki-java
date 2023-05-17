package top.ersut;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MultipleSelectorTest {

    int multipleServerSocketPort = 8615;

    @Test
    public void MultipleSelectorTest(){
        Thread.currentThread().setName("boss");

        AtomicInteger index = new AtomicInteger();
        //cpu核心数,建议通过配置文件调整，因为docker读取的核心数是物理主机的，不是容器真实的核心数，所以会导致获取错误
        int cpuCore = 4;
        WorkerLoop[] workerLoops = new WorkerLoop[cpuCore];
        for (int i = 0; i < workerLoops.length; i++) {
            WorkerLoop workerLoop = new WorkerLoop(i);
            workerLoops[i] = workerLoop;
        }
        try (
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                Selector selector = Selector.open();
                ){
            serverSocketChannel.bind(new InetSocketAddress(multipleServerSocketPort));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("serverSocketChannel start...");

            while (true){
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()){
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isAcceptable()){
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        log.info("new client:[{}]",socketChannel.getRemoteAddress());
                        workerLoops[index.getAndIncrement() % workerLoops.length].register(socketChannel);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < workerLoops.length; i++) {
                workerLoops[i].close();
            }
        }
    }

    @Test
    public void SocketClientTest(){
        try (
            SocketChannel socketChannel = SocketChannel.open();
        ){
            socketChannel.connect(new InetSocketAddress(multipleServerSocketPort));
            socketChannel.write(StandardCharsets.UTF_8.encode("ABCD"));
            log.info("client:[{}]",socketChannel.getLocalAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class WorkerLoop implements Runnable{
        /**
         * 线程对象
         */
        private Thread thread;
        /**
         * SocketChannel的选择器（多路复用）
         * 负责管理该线程的SocketChannel
         */
        private Selector worker;
        //索引，线程名使用
        private int index;
        //标识当前线程是否启动
        private volatile boolean starting = false;
        //任务队列，用于俩个线程之间的交互
        ConcurrentLinkedQueue<Runnable> registerTasks = new ConcurrentLinkedQueue<>();

        public void init() throws IOException {
            if(!starting){
                worker = Selector.open();
                thread = new Thread(this,"worker- "+index);
                thread.start();
                starting = true;
            }
        }

        public void register(SocketChannel socketChannel) throws IOException {
            init();
            registerTasks.add(() -> {
                try {
                    socketChannel.register(this.worker,SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            //结束Selector的阻塞，若当前阻塞中，那么结束阻塞；若当前未阻塞，那么下一次不阻塞
            worker.wakeup();
        }

        public WorkerLoop(int index) {
            this.index = index;
        }

        public void close(){
            if (worker.isOpen()){
                try {
                    worker.close();
                } catch (IOException e) {
                    log.error("worker close error",e);
                }
            }
        }


        @SneakyThrows
        @Override
        public void run() {
            while (worker.isOpen()){
                worker.select();
                //处理任务
                Runnable task = registerTasks.poll();
                //执行所有任务
                while (task != null) {
                    task.run();
                    //获取任务
                    task = registerTasks.poll();
                }
                Iterator<SelectionKey> keyIterator = worker.selectedKeys().iterator();
                while (keyIterator.hasNext()){
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isReadable()){
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
                    keyIterator.remove();
                }
            }
        }
    }

}
