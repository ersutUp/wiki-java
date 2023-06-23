package top.ersut.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
public class FutureAndPromiseTest {

    /**
     * JDK的Futter
     */
    @Test
    public void futureByJdkTest() throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future = service.submit(() -> {
            log.info("异步线程开始执行");
            Thread.sleep(1000);
            log.info("异步线程执行完毕");
            return 10;
        });
        log.info("主线程处理其他任务");
        log.info("等待异步线程结果：{}",future.get());
        log.info("整体程序执行完毕");
    }

    /**
     * netty的Future继承自JDK的Future
     * 它增强了JDK的Future，实现了更多状态的判断：isSuccess、isCancellable，以及监听器、非阻塞模式获取结果
     * Future 的 同步模式
     */
    @Test
    public void futureGetByNettyTest() throws ExecutionException, InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        EventLoop eventLoop = eventLoopGroup.next();

        io.netty.util.concurrent.Future future = eventLoop.submit(() -> {
            log.info("异步线程开始执行");
            Thread.sleep(1000);
            log.info("异步线程执行完毕");
            return 20;
        });

        log.info("主线程处理其他任务");
        log.info("等待异步线程结果：{}",future.get());
        log.info("整体程序执行完毕");
    }

    /**
     * Future 的 监听器模式（回调模式）
     */
    @Test
    public void futureListenerByNettyTest() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        EventLoop eventLoop = eventLoopGroup.next();

        io.netty.util.concurrent.Future future = eventLoop.submit(() -> {
            log.info("异步线程开始执行");
            Thread.sleep(1000);
            log.info("异步线程执行完毕");
            return 30;
        });

        log.info("主线程处理其他任务");
        future.addListener((GenericFutureListener<? extends io.netty.util.concurrent.Future<Integer>>) (future2) -> {
            log.info("等待异步线程结果：{}",future2.getNow());
        });
        log.info("关闭EventLoopGroup，并等待任务结束");
        //关闭EventLoopGroup，并等待任务结束
        io.netty.util.concurrent.Future<?> shutdownGracefullyFuture = eventLoopGroup.shutdownGracefully();
        shutdownGracefullyFuture.sync();
        log.info("整体程序执行完毕");
    }

    /**
     * Promise继承自Future
     * 它可以手动设置结果
     * @throws IOException
     */
    @Test
    public void promiseTest() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        EventLoop eventLoop = eventLoopGroup.next();

        Promise<String> promise = new DefaultPromise(eventLoop);

        promise.addListener((GenericFutureListener<? extends io.netty.util.concurrent.Future<String>>) (future2) -> {
            if (future2.isSuccess()) {
                log.info("等待异步线程结果：{}",future2.getNow());
            } else {
                log.error("异步线程手动抛出错误",future2.cause());
            }
        });

        eventLoop.execute(() -> {
            log.info("生成数据，判断是否生成的数大于50");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                promise.setFailure(e);
            }
            int random = (int)(Math.random() * 100);
            if(random > 50){
                promise.setSuccess("success:"+random);
            } else {
                promise.tryFailure(new Exception("error:"+random));
            }
            log.info("异步线程执行完毕");
        });
        log.info("主线程处理其他任务");
        log.info("关闭EventLoopGroup，并等待任务结束");
        //关闭EventLoopGroup，并等待任务结束
        io.netty.util.concurrent.Future<?> shutdownGracefullyFuture = eventLoopGroup.shutdownGracefully();
        shutdownGracefullyFuture.sync();
        log.info("整体程序执行完毕");
    }

}
