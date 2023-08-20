# Netty入门



## 1. 概述

### 1.1 Netty是什么

Netty是一个**异步的、基于事件驱动的**网络应用框架，用于快速开发**可维护、高性能**的网络服务器和客户端。

Netty是基于NIO实现的，上段话中“基于事件驱动的”指的是Selector的事件。

### 1.2 Netty的地位

Netty在Java网络应用框架的地位好比spring在JavaEE中的地位

以下框架都是使用了Netty，因为他们都有网络通信需求

* gRPC - rpc 框架
* Dubbo - rpc 框架
* Spring 5.x - flux api 完全抛弃了 tomcat ，使用 netty 作为服务器端
* Cassandra - nosql 数据库
* Spark - 大数据分布式计算框架
* Hadoop - 大数据分布式存储框架
* RocketMQ - ali 开源的消息队列
* ElasticSearch - 搜索引擎
* Zookeeper - 分布式协调框架

### 1.3 Netty的优势

- Netty vs NIO
  - NIO工作量大，Bug多
  - 需要自己构建协议
  - 解决TCP传输数据中的问题，例如：粘包、半包、拆包
  - NIO在Linux下，epoll 空轮询导致 CPU 100%的问题
  - 对API进行了增强，更易用，例如：FastThreadLocal => ThreadLocal，ByteBuf => ByteBuffer
- Netty vs 其他框架
  - Mina 由 Apache 维护，与 Netty 相比 Netty 的迭代速度更快，API更简洁。
  - 经久考验，Netty 诞生与2004年，至今已有19年的时间，
    - 2.x版本 2004年
    - 3.x版本 2008年
    - 4.x版本 2013年
    - 5.x版本 已废弃，没有明显的性能提升，且维护成本高

## 2. Hello Netty 

###  2.1 需求

- 服务端接收消息并打印
- 客户端向服务端发送 hello netty

引入依赖：

```xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.92.Final</version>
</dependency>
```

### 2.2 服务端

```java
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
```



### 2.3 客户端

```java
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
```

### 2.4 流程梳理

```mermaid
sequenceDiagram
participant s as 服务端
participant c as 客户端

s ->> s:创建启动器 new ServerBootstrap()
s ->> s:配置事件轮询组，NioEventLoopGroup: selector的多线程模式
s ->> s:配置服务端通道 channel()
s ->> s:配置与客户端channel的处理器 childHandler()
s ->> s:配置端口 bind()

c ->> c:创建启动器 new Bootstrap()
c ->> c:配置通道 channel()
c ->> c:配置处理器 handler()

c ->> s:连接服务端 connect()
c ->> c:阻塞线程等待成功连接服务端 sync()
s ->> s:ChannelInitializer.initChannel(NioSocketChannel ch) 
s ->> c:连接成功
c ->> c:结束阻塞
c ->> c:ChannelInitializer.initChannel(NioSocketChannel ch)
c ->> c:发送"hello netty"消息
c ->> c:将消息编码 StringEncoder.encode()
c ->> s:发送编码后的消息
s ->> s:解码消息 StringDecoder.decode()
s ->> s:打印解码后的消息 ChannelInboundHandlerAdapter.channelRead()

```

#### 💡 提示

树立正确的观念

**channel 、msg、pipeline 、handler、eventLoop的关系**

* 把 channel 理解为**数据的通道**
* 把 msg 理解为**通道中流动的数据**，最开始输入是 ByteBuf，但经过 pipeline **多道工序的加工**，会变成其它类型对象，最后输出又变成 ByteBuf
* 把 handler 理解为**数据的处理工序**
  * **工序有多道，合在一起就是 pipeline**，pipeline 负责发布事件（读、读取完成...）传播给每个 handler， handler 对自己感兴趣的事件进行处理（重写了相应事件处理方法）
  * handler 分 Inbound（输入） 和 Outbound（输出） 两类

* 把 eventLoop 理解为**处理数据的工人**
  * 工人可以管理多个 channel 的 io 操作，并且一旦工人负责了某个 channel，就要负责到底（绑定）
  * 工人既可以执行 io 操作，也可以进行任务处理，每位工人有任务队列，队列里可以堆放多个 channel 的待处理任务，任务分为普通任务、定时任务
  * **工人按照 pipeline 顺序，依次按照 handler 的规划（代码）处理数据**，可以为每道工序指定不同的工人

## 3. 组件

### 3.1 EventLoop

#### 3.1.1 事件循环对象

EventLoop本质上是一个任务执行器，同时维护了一个Selector，并且包含一个run方法来处理channel中源源不断的io事件

**继承关系**

![](./images/eventLoop-extend.png)

- 继承了`java.util.concurrent.ScheduledExecutorService`任务执行器，因此包含了所有的线程池方法
- 继承了`io.netty.util.concurrent.EventExecutor`，这个接口包含了：
  - `boolean inEventLoop(Thread thread);`：**判断一个线程是否属于该EventLoop**

#### 3.1.2 事件循环组对象

`EventLoopGroup`是一组`EventLoop`，通过`EventLoopGroup`的`register`方法将`channel`绑定到某一个

`EventLoop`，这个`channel`后续所有的io事件都由此`EventLoop`处理（保证了io事件处理时的线程安全）

**`EventLoopGroup`的实现类**

- NioEventLoopGroup：可处理io事件、普通任务和定时任务
- DefaultEventLoopGroup：可普通任务和定时任务

**简单的示例**

```java
EventLoopGroup group = new DefaultEventLoopGroup(2);
//第一个eventLoop
EventLoop eventLoop1 = group.next();
//第二个eventLoop
EventLoop eventLoop2 = group.next();
//第一个eventLoop
EventLoop eventLoop3 = group.next();
//第二个eventLoop
EventLoop eventLoop4 = group.next();

//由于只有两个线程所以只有两个eventLoop，那么第一次和第三次获取的是同一个eventLoop，同理第二次和第四次获取的是同一个eventLoop
Assert.assertEquals(eventLoop1,eventLoop3);
Assert.assertEquals(eventLoop2,eventLoop4);
```

**处理普通任务和定时任务**

```java
EventLoopGroup group = new DefaultEventLoopGroup(2);

//添加任务
group.next().execute(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    log.debug("给eventLoop添加任务");
});

//添加定时任务
group.next().scheduleAtFixedRate(
        () -> log.debug(String.valueOf(LocalDateTime.now().getSecond()))
        //多少秒后执行第一次
        ,0
        //每隔多久执行一次
        ,1
        //参数3的时间单位
        , TimeUnit.SECONDS
);

log.debug("主线程");

try {
    Thread.sleep(1000*5);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

控制台打印：

```tex
22:17:02.988 [main] DEBUG top.ersut.netty.EventLoopTest - 主线程
22:17:03.006 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 3
22:17:03.989 [defaultEventLoopGroup-2-1] DEBUG top.ersut.netty.EventLoopTest - 给eventLoop添加任务
22:17:03.989 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 3
22:17:04.988 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 4
22:17:05.988 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 5
22:17:06.989 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 6
22:17:07.989 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - 7
```

[示例代码#eventLoopGroupTest](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)

##### 💡 优雅关闭

优雅关闭 `shutdownGracefully` 方法。该方法会首先切换 `EventLoopGroup` 到关闭状态从而拒绝新的任务的加入，然后在任务队列的任务都处理完成后，停止线程的运行。从而确保整体应用是在正常有序的状态下退出的

##### 多个EventLoopGroup，处理不同的事件

[客户端代码](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)：

```java
//两个客户端各发送两条消息
public void client() throws InterruptedException {
    NioEventLoopGroup group = new NioEventLoopGroup();
    Channel channel = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            .connect(new InetSocketAddress(PORT))
            .sync()
            .channel();
    Channel channel2 = new Bootstrap()
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new StringEncoder());
                }
            })
            .connect(new InetSocketAddress(PORT))
            .sync()
            .channel();
    channel.writeAndFlush("123");
    Thread.sleep(1000);
    channel2.writeAndFlush("abc");
    Thread.sleep(1000);
    channel.writeAndFlush("456");
    Thread.sleep(1000);
    channel2.writeAndFlush("def");

    group.shutdownGracefully().sync();
    channel.close();
    channel2.close();
}
```

[服务端代码](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)：

```java
public static void groupServer(){
    new ServerBootstrap()
            /**
             * 参数1 parentGroup：处理 NioServerSocketChannel 的accept
             * 参数2 childGroup：处理 NioSocketChannel 的 读写
             */
            .group(new NioEventLoopGroup(1),new NioEventLoopGroup(2))
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf message = (ByteBuf) msg;
                            log.debug(message.toString(StandardCharsets.UTF_8));
                        }
                    });
                }
            })
            .bind(PORT);
}
```

打印信息：

```tex
22:36:20.907 [nioEventLoopGroup-3-1] DEBUG top.ersut.netty.EventLoopTest - 123
22:36:21.891 [nioEventLoopGroup-3-2] DEBUG top.ersut.netty.EventLoopTest - abc
22:36:22.890 [nioEventLoopGroup-3-1] DEBUG top.ersut.netty.EventLoopTest - 456
22:36:23.889 [nioEventLoopGroup-3-2] DEBUG top.ersut.netty.EventLoopTest - def
```

##### 💡进程名解释

nioEventLoopGroup-3-2

- 其中的”3“代表第3个进程池
- 其中的“2”代表EventLoopGroup中的第二个EventLoop

**通过打印的信息可以看出同一个channel中的信息在固定的EventLoop中处理，即channel与EventLoop绑定**

图解:

![](images/0042.png)

##### 给通道处理器指定EventLoopGroup

客户端代码与上边的一致

[服务端代码](./netty_demo/src/main/test/top/ersut/netty/EventLoopTest.java)：

```java
public static void pipelineAppointGroupServer(){
    EventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(2);
    new ServerBootstrap()
            /**
             * 参数1 parentGroup：处理 NioServerSocketChannel 的accept
             * 参数2 childGroup：处理 NioSocketChannel 的 读写
             */
            .group(new NioEventLoopGroup(),new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf message = (ByteBuf) msg;
                            log.debug(message.toString(StandardCharsets.UTF_8));
                            //传给pipeline中的下一个处理器
                            super.channelRead(ctx,msg);
                        }
                    });
                    ch.pipeline().addLast(defaultEventLoopGroup,"default",new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf message = (ByteBuf) msg;
                            log.debug(message.toString(StandardCharsets.UTF_8)+"-default");
                        }
                    });
                }
            })
            .bind(PORT);
}
```

- **当多个通道处理器时，需要通过`super.channelRead(ctx,msg)`将消息传递给下一个处理器**

- **通过`addLast(EventExecutorGroup group, String name, ChannelHandler handler)`方法给通道处理器指定EventLoopGroup**
  - 参数1：指定EventLoopGroup
  - 参数2：给通道处理器指定名称
  - 参数3：通道处理器

控制台打印：

```tex
22:54:55.282 [nioEventLoopGroup-4-1] DEBUG top.ersut.netty.EventLoopTest - 123
22:54:55.283 [defaultEventLoopGroup-2-1] DEBUG top.ersut.netty.EventLoopTest - 123-default
22:54:56.246 [nioEventLoopGroup-4-2] DEBUG top.ersut.netty.EventLoopTest - abc
22:54:56.247 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - abc-default
22:54:57.247 [nioEventLoopGroup-4-1] DEBUG top.ersut.netty.EventLoopTest - 456
22:54:57.247 [defaultEventLoopGroup-2-1] DEBUG top.ersut.netty.EventLoopTest - 456-default
22:54:58.247 [nioEventLoopGroup-4-2] DEBUG top.ersut.netty.EventLoopTest - def
22:54:58.247 [defaultEventLoopGroup-2-2] DEBUG top.ersut.netty.EventLoopTest - def-default
```

**通过打印的信息可以看出名为 default 的通道处理器使用了其他线程**

图解：

![](./images/0041.png)

##### 💡多个通道处理器之间是怎么切换EventLoop的

追踪`super.channelRead(ctx,msg);`方法可以找到如下源码

```java
static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
    final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
    EventExecutor executor = next.executor();
    //判断下一个EventLoop与当前EventLoop是否为同一个线程
    if (executor.inEventLoop()) {
        //是同一个，直接运行
        next.invokeChannelRead(m);
    } else {
        //不是同一个，将需要执行的代码作为任务添加到给下一个EventLoop(切换EventLoop)
        executor.execute(new Runnable() {
            @Override
            public void run() {
                next.invokeChannelRead(m);
            }
        });
    }
}
```

* 如果两个 handler 绑定的是同一个线程，那么就直接调用
* 否则，把要调用的代码封装为一个任务对象，由下一个 handler 的线程来调用

### 3.2 Channel

注意这里的Channel是netty定义的，不是Java自己的Channel

Channel的主要方法：

- eventLoop()：Channel所在的eventLoop
- close()：用来关闭Channel
- closeFuture()：Channel关闭成功后的回调
  - sync()：**同步阻塞**，等待channel关闭
  - addListener()：**异步非阻塞**，等待 channel 关闭
- writeAndFlush()：写入内容并发送
- wirte():写入数据

**封装客户端启动器**

```java
private static final int PORT = 15218;

public static Bootstrap bootstrap(){
    return bootstrap(new NioEventLoopGroup());
}
public static Bootstrap bootstrap(NioEventLoopGroup nioEventLoopGroup){
    return new Bootstrap()
            .group(nioEventLoopGroup)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //打印详细日志
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new StringEncoder());
                }
            });
}
```

**LoggingHandler是netty的日志处理器**，可以查看channel的运行情况等

[示例代码#bootstrap](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

#### 3.2.1 Channel的连接

```java
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

Channel channel = channelFuture.channel();
channel.writeAndFlush("not sync");
log.info("channel isActive:{}",channel.isActive());
```

[示例代码PrematureGetChannelTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-17 22:09:09 [main] INFO  top.ersut.netty.ChannelTest - channel isActive:false
2023-06-17 22:09:09 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x35a9f64c] REGISTERED
2023-06-17 22:09:09 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x35a9f64c] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-17 22:09:09 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x35a9f64c, L:/192.168.123.137:52198 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
```

`id: 0x35a9f64c, L:/192.168.123.137:52198 - R:0.0.0.0/0.0.0.0:15218] ACTIVE`这行日志代表channel成功连接

##### 上述代码存在的问题

- **过早的获取channel，导致消息发送失败**

##### 问题分析

通过上述日志中 `channel isActive:false`可以看到 channel 是非活动状态，发送消息的代码是在此条日志之前

非活动状态的channel代表客户端与服务端还未成功连接，所以这条消息发送失败。

##### 解决方案：ChannelFuture

JDK的Future是获取异步线程的结果，他与ChannelFuture的关系：

![](./images/ChannelFuture.png)

ChannelFuture继承自JDK的Future，并且进行了增强。

**ChannelFuture中常用的方法**

- channel()：获取channel
- sync()：同步等待结果
- addListener()：添加监听器

**ChannelFuture的获取**

```
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));
```

通过connect方法获取的ChannelFuture

**connect方法是异步的，那么不等待连接成功就返回了，因此通过ChannelFuture.channel()获取channel并不一定是已连接的。**

##### **方案1：通过sync()同步等待结果**

```java
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

//新增代码，等待异步线程的结果
channelFuture.sync();
Channel channel = channelFuture.channel();
channel.writeAndFlush("sync");
log.info("channel isActive:{}",channel.isActive());
```

[示例代码ChannelBySyncTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```java
2023-06-17 23:31:49.861 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e] REGISTERED
2023-06-17 23:31:49.865 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-17 23:31:49.871 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e, L:/192.168.123.137:54507 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
2023-06-17 23:31:49.877 [main] INFO  top.ersut.netty.ChannelTest - channel isActive:true
2023-06-17 23:31:49.895 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e, L:/192.168.123.137:54507 - R:0.0.0.0/0.0.0.0:15218] WRITE: 4B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 73 79 6e 63                                     |sync            |
+--------+-------------------------------------------------+----------------+
2023-06-17 23:31:49.896 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x5f7acf4e, L:/192.168.123.137:54507 - R:0.0.0.0/0.0.0.0:15218] FLUSH
```

服务器收到了消息：

```tex
2023-06-17 23:32:42.395 [nioEventLoopGroup-3-1] INFO  top.ersut.netty.ChannelTest - sync
```

##### **方案2：通过addListener()添加监听器（回调方式）**

```java
ChannelFuture channelFuture = bootstrap().connect(new InetSocketAddress(PORT));

//等待连接成功
channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
    Channel channel = channelFuture1.channel();
    channel.writeAndFlush("ChannelFutureListener");
    log.info("channel isActive:{}",channel.isActive());
});
```

[示例代码ChannelByListenerTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6] REGISTERED
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6, L:/192.168.123.137:54912 - R:0.0.0.0/0.0.0.0:15218] WRITE: 21B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 43 68 61 6e 6e 65 6c 46 75 74 75 72 65 4c 69 73 |ChannelFutureLis|
|00000010| 74 65 6e 65 72                                  |tener           |
+--------+-------------------------------------------------+----------------+
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6, L:/192.168.123.137:54912 - R:0.0.0.0/0.0.0.0:15218] FLUSH
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.ChannelTest - channel isActive:true
2023-06-17 23:48:29 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0xd294f3e6, L:/192.168.123.137:54912 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
```

服务器收到了消息：

```tex
2023-06-17 23:48:29.858 [nioEventLoopGroup-3-2] INFO  top.ersut.netty.ChannelTest - ChannelFutureListener
```

##### 💡两种方案线程为什么不一样

比较两个方案中，`log.info("channel isActive:{}",channel.isActive());`都是打印的 channel isActive:true ，但是他们的线程不一样：

- sync()方式是main线程（主线程），该方式是同步等待结果，所以在主线程打印。
- addListener()方式是 nioEventLoopGroup-2-1 线程，该方式是在子线程中连接成功后直接运行我们的代码，所以在 nioEventLoopGroup-2-1 线程

#### 3.2.2 Channel的关闭

```java
NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));
 
//等待连接成功
channelFuture.sync();
Channel channel = channelFuture.channel();
channel.writeAndFlush("channelClose");
log.info("close......");
channel.close();
//期望channel成功关闭后再运行
log.info("closed");
```

[示例代码ChannelCloseTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889] REGISTERED
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
2023-06-18 13:03:48 [main] INFO  top.ersut.netty.ChannelTest - close......
2023-06-18 13:03:48 [main] INFO  top.ersut.netty.ChannelTest - closed
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] WRITE: 12B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 68 61 6e 6e 65 6c 43 6c 6f 73 65             |channelClose    |
+--------+-------------------------------------------------+----------------+
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] FLUSH
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] CLOSE
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 ! R:0.0.0.0/0.0.0.0:15218] INACTIVE
2023-06-18 13:03:48 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x3908f889, L:/192.168.123.137:53074 ! R:0.0.0.0/0.0.0.0:15218] UNREGISTERED
```

`[id: 0x3908f889, L:/192.168.123.137:53074 - R:0.0.0.0/0.0.0.0:15218] CLOSE`这行日志代表channel成功关闭

##### 上述代码存在的问题

1. 由于客户端关闭是在NioEventLoop线程中进行的，所以主线程中不能正确的执行关闭后的任务
2. 关闭Channel后进程没有终止

##### 问题1分析

通过日志我们可以发现`log.info("closed");`执行在channel成功关闭之前，与我们的期望（channel成功关闭后再运行）不一致

##### 问题1的解决方案：closeFuture()

通过channel的closeFuture()可以获取关闭channel的ChannelFuture

解决方案

- 通过sync()同步等待结果，[示例代码ChannelCloseFutureSyncTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)
- 通过addListener()添加监听器[示例代码ChannelCloseFutureListenerTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

此处不再过多叙述与connect（连接服务端）返回的ChannelFuture是一个类

- connect()的ChannelFuture是关于channel连接成功的
- closeFuture()的ChannelFuture是关于channel关闭成功的

##### 问题2分析

除主线程外，每个 **EventLoop 都有自己的线程**，那么当主线程代码执行完成后，由于  **EventLoop 的线程还未结束，所以进程不会结束。**

**只有结束所有的线程，进程才会终止。**

##### 问题2的解决方案

之前提到过NioEventLoopGroup提供了shutdownGracefully方法用来**停止其包含的所有EventLoop对应的线程**。

注意：shutdownGracefully方法**同时会关闭channel**

```java
NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
ChannelFuture channelFuture = bootstrap(eventLoopGroup).connect(new InetSocketAddress(PORT));

//等待连接成功
channelFuture.sync();
Channel channel = channelFuture.channel();
channel.writeAndFlush("channelCloseFutureListener");
log.info("channel isActive:{}",channel.isActive());

ChannelFuture closeFuture = channel.closeFuture();
closeFuture.addListener((ChannelFutureListener) future -> {
    Channel channel1 = future.channel();
    log.info("channel isActive:{}",channel1.isActive());
    log.info("closed");
});

log.info("close......");
//eventLoopGroup中还有线程，关闭eventLoopGroup中的线程，同时会关闭channel
eventLoopGroup.shutdownGracefully();
```

[示例代码ChannelCloseFutureListenerTest](./netty_demo/src/main/test/top/ersut/netty/ChannelTest.java)

控制台打印：

```tex
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3] REGISTERED
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3] CONNECT: 0.0.0.0/0.0.0.0:15218
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 - R:0.0.0.0/0.0.0.0:15218] ACTIVE
2023-06-18 16:41:53 [main] INFO  top.ersut.netty.ChannelTest - close......
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 - R:0.0.0.0/0.0.0.0:15218] WRITE: 22B
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 63 68 61 6e 6e 65 6c 43 6c 6f 73 65 46 75 74 75 |channelCloseFutu|
|00000010| 72 65 53 79 6e 63                               |reSync          |
+--------+-------------------------------------------------+----------------+
2023-06-18 16:41:53 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 - R:0.0.0.0/0.0.0.0:15218] FLUSH
2023-06-18 16:41:56 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 ! R:0.0.0.0/0.0.0.0:15218] INACTIVE
2023-06-18 16:41:56 [nioEventLoopGroup-2-1] DEBUG i.n.handler.logging.LoggingHandler - [id: 0x1a1b49c3, L:/192.168.123.137:59394 ! R:0.0.0.0/0.0.0.0:15218] UNREGISTERED
2023-06-18 16:41:58 [main] INFO  top.ersut.netty.ChannelTest - closed
```

成功解决！

### 3.3 Future&Promise

这是Netty在异步是使用的两个接口

**继承关系图：**

![](./images/Promise-extend.png)

注意：**Netty的Future接口与JDK的Future接口同名**，并且Netty的Future继承了JDK的Future，Promise继承了Netty的Future

整体来说，**这三个接口是用来存储异步任务的结果**

- JDK的Future：必须同步等待任务结束才能获取结果（成功or失败）
- Netty的Future：增强了JDK的Future，可以同步等待任务结束获取结果，也可以异步等待（Listener）任务获取结果
- Promise：拥有Netty的Future中所有功能，而且脱离了任务独立存在，作为两个线程（两个任务）间传递结果的容器。

方法的对比

| 功能/名称    | jdk Future                     | netty Future                                                 | Promise      |
| ------------ | ------------------------------ | ------------------------------------------------------------ | ------------ |
| cancel       | 取消任务                       | 继承                                                         | 继承         |
| isCanceled   | 任务是否取消                   | 继承                                                         | 继承         |
| isDone       | 任务是否完成，不能区分成功失败 | 继承                                                         | 继承         |
| get          | 获取任务结果，阻塞等待         | 继承                                                         | 继承         |
| getNow       | -                              | 获取任务结果，非阻塞，还未产生结果时返回 null                | 继承         |
| await        | -                              | 等待任务结束，如果任务失败，不会抛异常，而是通过 isSuccess 判断 | 继承         |
| sync         | -                              | 等待任务结束，如果任务失败，抛出异常                         | 继承         |
| isSuccess    | -                              | 判断任务是否成功                                             | 继承         |
| cause        | -                              | 获取失败信息，非阻塞，如果没有失败，返回null                 | 继承         |
| addLinstener | -                              | 添加回调，异步接收结果                                       | 继承         |
| setSuccess   | -                              | -                                                            | 设置成功结果 |
| setFailure   | -                              | -                                                            | 设置失败结果 |

#### JDK的Future示例

```java
ExecutorService service = Executors.newFixedThreadPool(2);
Future<Integer> future = service.submit(() -> {
    log.info("异步线程开始执行");
    Thread.sleep(1000);
    log.info("异步线程执行完毕");
    return 10;
});
log.info("主线程处理其他任务");
//同步阻塞，等待结果
log.info("等待异步线程结果：{}",future.get());
log.info("整体程序执行完毕");
```

[示例代码futureByJdkTest](./netty_demo/src/main/test/top/ersut/netty/FutureAndPromiseTest.java)

控制台打印：

```tex
2023-06-23 10:07:47 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureByJdkTest:33 - 主线程处理其他任务
2023-06-23 10:07:47 [pool-2-thread-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureByJdkTest$0:28 - 异步线程开始执行
2023-06-23 10:07:48 [pool-2-thread-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureByJdkTest$0:30 - 异步线程执行完毕
2023-06-23 10:07:48 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureByJdkTest:34 - 等待异步线程结果：10
2023-06-23 10:07:48 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureByJdkTest:35 - 整体程序执行完毕
```

可以看出`future.get()`同步阻塞了主线程，当异步线程结束时`future.get()`获取了其结果。

#### Netty的Future示例

```java
EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
EventLoop eventLoop = eventLoopGroup.next();

io.netty.util.concurrent.Future future = eventLoop.submit(() -> {
    log.info("异步线程开始执行");
    Thread.sleep(1000);
    log.info("异步线程执行完毕");
    return 20;
});

log.info("主线程处理其他任务");
//同步阻塞，等待结果
log.info("等待异步线程结果：{}",future.get());
log.info("整体程序执行完毕");
```

[示例代码futureGetByNettyTest](./netty_demo/src/main/test/top/ersut/netty/FutureAndPromiseTest.java)

控制台打印：

```tex
2023-06-23 10:12:24 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureGetByNettyTest$1:49 - 异步线程开始执行
2023-06-23 10:12:24 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureGetByNettyTest:55 - 主线程处理其他任务
2023-06-23 10:12:25 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureGetByNettyTest$1:51 - 异步线程执行完毕
2023-06-23 10:12:25 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureGetByNettyTest:56 - 等待异步线程结果：20
2023-06-23 10:12:25 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureGetByNettyTest:57 - 整体程序执行完毕
```

由于Netty的Future继承了JDK的Future，此处`future.get()`与Future(JDK)的作用一致。

**注意**：查看`log.info("等待异步线程结果：{}",future.get());`这行日志的线程是main，所以**处理结果是在主线程中进行的**

#### Future（Netty）的增强功能示例

##### 监听器（异步线程中处理结果）

```java
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
```

[示例代码futureListenerByNettyTest](./netty_demo/src/main/test/top/ersut/netty/FutureAndPromiseTest.java)

控制台打印：

```TEX
2023-06-23 10:30:30 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureListenerByNettyTest$2:69 - 异步线程开始执行
2023-06-23 10:30:30 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureListenerByNettyTest:75 - 主线程处理其他任务
2023-06-23 10:30:30 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureListenerByNettyTest:79 - 关闭EventLoopGroup，并等待任务结束
2023-06-23 10:30:31 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureListenerByNettyTest$2:71 - 异步线程执行完毕
2023-06-23 10:30:31 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$futureListenerByNettyTest$3:77 - 等待异步线程结果：30
2023-06-23 10:30:33 [main] INFO  top.ersut.netty.FutureAndPromiseTest.futureListenerByNettyTest:83 - 整体程序执行完毕
```

**Future.getNow()**：同步非阻塞的情况下获取结果

通过日志可以看出`log.info("等待异步线程结果：{}",future2.getNow());`这行日志的线程是nioEventLoopGroup-2-1（即异步线程），与上方**Netty的Future示例**中该行日志所使用线程不一致，所以**处理结果是在异步线程中进行的**

#### Promise示例

```java
EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
EventLoop eventLoop = eventLoopGroup.next();

Promise<String> promise = new DefaultPromise(eventLoop);//1处

//2除
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
        //3处
        promise.setSuccess("success:"+random);
    } else {
        //4处
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
```

[示例代码promiseTest](./netty_demo/src/main/test/top/ersut/netty/FutureAndPromiseTest.java)

控制台打印：

- 失败的情况：

```tex
2023-06-23 11:07:40 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$5:107 - 生成数据，判断是否生成的数大于50
2023-06-23 11:07:40 [main] INFO  top.ersut.netty.FutureAndPromiseTest.promiseTest:121 - 主线程处理其他任务
2023-06-23 11:07:40 [main] INFO  top.ersut.netty.FutureAndPromiseTest.promiseTest:122 - 关闭EventLoopGroup，并等待任务结束
2023-06-23 11:07:41 [nioEventLoopGroup-2-1] ERROR top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$4:102 - 异步线程手动抛出错误
java.lang.Exception: error:38
	at top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$5(FutureAndPromiseTest.java:117)
	at io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:163)
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:416)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:515)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:918)
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Thread.java:745)
2023-06-23 11:07:41 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$5:119 - 异步线程执行完毕
2023-06-23 11:07:43 [main] INFO  top.ersut.netty.FutureAndPromiseTest.promiseTest:126 - 整体程序执行完毕
```

- 成功的情况：

```tex
2023-06-23 11:09:17 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$5:107 - 生成数据，判断是否生成的数大于50
2023-06-23 11:09:17 [main] INFO  top.ersut.netty.FutureAndPromiseTest.promiseTest:121 - 主线程处理其他任务
2023-06-23 11:09:17 [main] INFO  top.ersut.netty.FutureAndPromiseTest.promiseTest:122 - 关闭EventLoopGroup，并等待任务结束
2023-06-23 11:09:18 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$4:100 - 等待异步线程结果：success:88
2023-06-23 11:09:18 [nioEventLoopGroup-2-1] INFO  top.ersut.netty.FutureAndPromiseTest.lambda$promiseTest$5:119 - 异步线程执行完毕
2023-06-23 11:09:20 [main] INFO  top.ersut.netty.FutureAndPromiseTest.promiseTest:126 - 整体程序执行完毕
```

##### 代码分析

Promise是new出来的（1处的代码），所以可以在任务之前添加监听器（2处的代码）。而且它可以由用户指定结果，成功（3处的代码）or失败（处的代码）

##### Promise与future相比

- Promise是可以自己创建的
- Promise由用户设定结果

### 3.4 ChannelHandler & Pipeline

**ChannelHandler**是用来处理channel上的各种事件，分为入站（inbound）和出站（outbound）两种处理器。

- 出站处理器：继承ChannelOutboundHandlerAdapter的子类，用来处理发送的消息即`Channel.writeAndFlush(Object msg)`
- 入站处理器：继承ChannelInboundHandlerAdapter的子类，用来处理接受到的消息。

**Pipeline**中存储了所有的ChannelHandler，这些ChannelHandler是串起来（双向链表）的

- Pipeline在创建的时候添加了两个默认处理器（head、tail），head在最前，tail在最尾部，这两个的位置不受新处理器的影响

#### Pipeline添加处理器的方法

| 方法                                                         | 描述                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| addLast(ChannelHandler handler)                              | 在tail处理器前方添加一个处理器                               |
| addLast(String name,ChannelHandler handler)                  | 在tail处理器前方添加一个自定义名称的处理器                   |
| addLast(EventExecutorGroup group,String name,ChannelHandler handler) | 在tail处理器前方添加一个自定义名称的处理器,并给处理器指定事件执行组（实现类有：NioEventLoopGroup、DefaultEventLoopGroup） |

#### EmbeddedChannel类

这个类是一个用来测试处理器的类

通过`EmbeddedChannel.pipeline().addxxx(ChannelHandler handler)`添加处理器，此处与正常的Channel一致

##### 入站以及出站的方法

| 方法                      | 描述               |
| ------------------------- | ------------------ |
| writeInbound(Object msg)  | 发送入站的测试消息 |
| readInbound()             | 接收入站消息       |
| writeOutbound(Object msg) | 发送出站的测试消息 |
| readOutbound()            | 接收出战消息       |

#### 处理器的顺序

##### 入站处理器顺序

```java
EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline()
                .addLast("inbound-1",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("客户端收到消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                        log.info("第一个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);//1处
                    }
                })
                .addLast("inbound-2",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("第二个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);//2处
                    }
                })
                .addLast("inbound-3",new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.info("第三个自定义入站处理器:{}",ctx.name());
                        super.channelRead(ctx, msg);//3处
                    }
                });
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("embeddedChannel-inbound".getBytes(StandardCharsets.UTF_8)));
```

[示例代码inboundByEmbeddedChannelTest](./netty_demo/src/main/test/top/ersut/netty/InboundAndOutboundTest.java)

当前处理器的存储顺序为 head <---> inbound-1 <---> inbound-2 <---> inbound-3 <---> tail，**<--->代表双向链表**

**1处、2处、3处的`super.channelRead(ctx, msg);`是为了传递数据到下一个处理器，没有这行代码无法执行下一个处理器**

控制台打印：

```tex
2023-06-24 10:12:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.channelRead:139 - 客户端收到消息：embeddedChannel-inbound
2023-06-24 10:12:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.channelRead:140 - 第一个自定义入站处理器:inbound-1
2023-06-24 10:12:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.channelRead:147 - 第二个自定义入站处理器:inbound-2
2023-06-24 10:12:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.channelRead:154 - 第三个自定义入站处理器:inbound-3
```

可以看到，**入站处理器是从head开始找，即从前往后找**

##### 出站处理器顺序

```java
EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline()
                .addLast("outbound-1",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("客户端发送消息：{}",((ByteBuf)msg).toString(StandardCharsets.UTF_8));
                        log.info("第一个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                })
                .addLast("outbound-2",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("第二个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                })
                .addLast("outbound-3",new ChannelOutboundHandlerAdapter() {
                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        log.info("第三个自定义出站处理器:{}",ctx.name());
                        super.write(ctx, msg, promise);
                    }
                });
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("embeddedChannel-outbound".getBytes(StandardCharsets.UTF_8)));
```

[示例代码outboundByEmbeddedChannelTest](./netty_demo/src/main/test/top/ersut/netty/InboundAndOutboundTest.java)

当前处理器的存储顺序为 head <---> outbound-1 <---> outbound-2 <---> outbound-3 <---> tail

控制台打印：

```tex
2023-06-24 10:14:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.write:184 - 第三个自定义出站处理器:outbound-3
2023-06-24 10:14:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.write:177 - 第二个自定义出站处理器:outbound-2
2023-06-24 10:14:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.write:169 - 客户端发送消息：embeddedChannel-outbound
2023-06-24 10:14:38 [main] INFO  t.ersut.netty.InboundAndOutboundTest.write:170 - 第一个自定义出站处理器:outbound-1
```

**与入站处理器的顺序不同，出站处理器的顺序是从tail往前开始找，即从后往前找**

##### 总结

- Pipeline中的ChannelInboundHandlerAdapter是正序执行的
- Pipeline中的ChannelOutboundHandlerAdapter是倒序执行的

#### Pipeline中的处理器是如何组成双向链表的

##### 查看`addLast`的源码

DefaultChannelPipeline中部分源码

```java
public class DefaultChannelPipeline implements ChannelPipeline {
	...
        
    public final ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
        final AbstractChannelHandlerContext newCtx;
        ...
        //将ChannelHandler包装为AbstractChannelHandlerContext
        newCtx = newContext(group, filterName(name, handler), handler);
        //处理新的AbstractChannelHandlerContext中的双向链表
        addLast0(newCtx);
        ...
    }
    
    ...
        
}
```

##### 为什么要包装ChannelHandler？

因为通过AbstractChannelHandlerContext维护的双向链表

看`newCtx = newContext(group, filterName(name, handler), handler);`的源码

```java
public class DefaultChannelPipeline implements ChannelPipeline {
	...
	
    private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
        return new DefaultChannelHandlerContext(this, childExecutor(group), name, handler);
    }
    
    ...
}
```

DefaultChannelHandlerContext类的继承关系

![](./images/DefaultChannelHandlerContext-extend.png)

查看AbstractChannelHandlerContext部分源码：

```java
abstract class AbstractChannelHandlerContext implements ChannelHandlerContext, ResourceLeakHint {
    ...
    
    //上一个AbstractChannelHandlerContext
    volatile AbstractChannelHandlerContext next;
    //下一个AbstractChannelHandlerContext
    volatile AbstractChannelHandlerContext prev;
    
    ...
}
```

通过这两个属性维护的双向链表

##### 如何证明是通过next、prev维护的链表？

继续查看`addLast`方法中的`addLast0`方法的源码

```java
public class DefaultChannelPipeline implements ChannelPipeline {
    ...
        
    //默认的两个处理器
    final AbstractChannelHandlerContext head;
    final AbstractChannelHandlerContext tail;
    
    ...
        
    protected DefaultChannelPipeline(Channel channel) {
        ...
        //初始化默认处理器
        tail = new TailContext(this);
        head = new HeadContext(this);
        //维护双向链表
        head.next = tail;
        tail.prev = head;
    }
    
    ...
         
    private void addLast0(AbstractChannelHandlerContext newCtx) {
        //当前处理器的顺序 head <---> ... <---> tail
        AbstractChannelHandlerContext prev = tail.prev;
        newCtx.prev = prev;
        newCtx.next = tail;
        prev.next = newCtx;
        tail.prev = newCtx;
        //经过上边代码后处理器的顺序 head <---> ... <---> newCtx <---> tail
    }
    
    ...
}
```

代码图解：

![](./images/handlerDoublyLinkedList.png)

通过图解可以看到通过 **处理器上下文 中的next、prev属性实现的双向链表**，另外`addLast0`是**将处理器添加在tail处理器之前，并不是真正的尾部**

##### 总结

1. 在创建Pipeline时，创建了默认的两个处理器，head以及tail，head在最前，tail在最尾部，并且位置不会改变
2. 通过将处理器（ChannelHandler）包装为 处理器上文（AbstractChannelHandlerContext），设置其next、prev属性实现的双向链表

#### 实际应用

##### 需求

将学生信息转换为json并使用Base64进行加密，发送给服务端；服务端解析数据，最终包装成学生信息

学生信息：

| 字段 | 内容 |
| ---- | ---- |
| name | 张三 |
| age  | 18   |
| sex  | 男   |

#### 解析需求

客户端发送数据：

1. 学生信息实体转json（第一个出站处理器）
2. json转Base64（第二个出站处理器）

服务端接收数据：

1. 接收到Base64数据转换为Json
2. Json转换为学生信息实体

#### 代码

学生信息实体：

```java
@Data
@Builder
private static class StudentInfo{
    private String name;
    private int age;
    private String sex;
}
```

##### [客户端代码(clientTest)](./netty_demo/src/main/test/top/ersut/netty/InboundAndOutboundExampleTest.java)：

```java
Channel channel = new Bootstrap()
        .group(new NioEventLoopGroup())
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                ch.pipeline()
                        .addLast("outbound-3",new ChannelOutboundHandlerAdapter() {
                            //转换为ByteBuf，因为发送的数据必须是ByteBuf类型
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                String base64 = (String) msg;
                                ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer().writeBytes(base64.getBytes(StandardCharsets.UTF_8));
                                super.write(ctx, byteBuf , promise);
                            }
                        })
                        .addLast("outbound-2",new ChannelOutboundHandlerAdapter() {
                            //json转base64
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                String str = (String) msg;
                                String encode = Base64.encode(str);
                                log.info("student base64:{}",encode);
                                super.write(ctx, encode, promise);
                            }
                        })
                        .addLast("outbound-1",new ChannelOutboundHandlerAdapter() {
                            //实体转json
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
```

使用了3个出站处理器

1. 学生实体转换json的出站处理器
2. json转换Base64的出站处理器
3. Base64转换为ByteBuf的出站处理器，之所以需要这个处理器是因为**发送的数据必须是ByteBuf类型**

控制台打印：

```tex
2023-06-24 14:28:31 [main] INFO  t.e.n.InboundAndOutboundExampleTest.clientTest:85 - studentInfo:InboundAndOutboundExampleTest.StudentInfo(name=张三, age=18, sex=男)
2023-06-24 14:28:31 [nioEventLoopGroup-2-1] INFO  t.e.n.InboundAndOutboundExampleTest.write:68 - student json:{"name":"张三","age":18,"sex":"男"}
2023-06-24 14:28:31 [nioEventLoopGroup-2-1] INFO  t.e.n.InboundAndOutboundExampleTest.write:59 - student base64:eyJuYW1lIjoi5byg5LiJIiwiYWdlIjoxOCwic2V4Ijoi55S3In0=
```

##### [服务端代码(main)](./netty_demo/src/main/test/top/ersut/netty/InboundAndOutboundExampleTest.java)：

```java
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
```

**使用了3个入站处理器**

1. **ByteBuf转字符串的入站处理器**
2. **Base64转json的入站处理器**
3. **json转学生实体的入站处理器**

控制台打印：

```tex
2023-06-24 14:28:31 [nioEventLoopGroup-2-4] INFO  t.e.n.InboundAndOutboundExampleTest.channelRead:109 - msg byteBuf:PooledUnsafeDirectByteBuf(ridx: 0, widx: 52, cap: 1024)
2023-06-24 14:28:31 [nioEventLoopGroup-2-4] INFO  t.e.n.InboundAndOutboundExampleTest.channelRead:118 - msg base64:eyJuYW1lIjoi5byg5LiJIiwiYWdlIjoxOCwic2V4Ijoi55S3In0=
2023-06-24 14:28:31 [nioEventLoopGroup-2-4] INFO  t.e.n.InboundAndOutboundExampleTest.channelRead:127 - msg json:{"name":"张三","age":18,"sex":"男"}
2023-06-24 14:28:31 [nioEventLoopGroup-2-4] INFO  t.e.n.InboundAndOutboundExampleTest.channelRead:129 - studentInfo:InboundAndOutboundExampleTest.StudentInfo(name=张三, age=18, sex=男)
```

### 3.5 ByteBuf

ByteBuf是JDK的ByteBuffer的增强，它也**支持两种创建方式：直接在内存中创建、在堆中创建**，它支持自动扩容、同时读写（避免了ByteBuffer切换模式的麻烦）

#### 3.5.1 创建

```java
ByteBuf directBuffer = ByteBufAllocator.DEFAULT.buffer();
```

##### 环境变量

`-Dio.netty.noPreferDirect`

- true：不适用直接内存
  - [示例代码#createByteBufNoPreferDirectTest](netty_demo/src/main/test/top/ersut/netty/ByteBufTest.java)
- false（默认值）：使用直接内存

##### 关于安卓端

安卓端不支持直接内存

[示例代码#createByteBufInAndroidTest](netty_demo/src/main/test/top/ersut/netty/ByteBufTest.java)

#### 3.5.2 直接内存、堆内存的优缺点

##### 堆内存

以下代码可以创建一个堆内存的ByteBuf

```java
ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
```

缺点：

- 读写效率低，因为在堆内存中使用时需要复制到主存中
- 另外受GC影响会进行复制等

##### 直接内存

以下代码可以创建一个直接内存的ByteBuf

```java
ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer();
```

优点：

- 读写效率高，由于在主存中直接创建，省去了堆内存复制到主存的步骤
- 因为不受GC管理，所以减轻GC的压力

缺点：

- 创建和销毁代价大，需要配合池化功能使用
- 操作不当可能引起内存溢gggna出，因为不受GC的管理，要及时清理

#### 3.5.3 池化

池化的意义是重复使用ByteBuf

- 没有池化，那每次都需要创建新的ByteBuf实例，对于直接内存来说创建和销毁的代价很大；并且高并发时容易内存溢出。
- 使用池化可以重用ByteBuf的实例，减少了内存溢出的可能。

##### 环境变量

`-Dio.netty.allocator.type`

- pooled：启用池化功能
- unpooled：关闭池化功能
  - [示例代码#createByteBufUnpooledTest](netty_demo/src/main/test/top/ersut/netty/ByteBufTest.java)

**windows和Linux默认启用池化功能**

[示例代码#createByteBufNotAndroidTest](netty_demo/src/main/test/top/ersut/netty/ByteBufTest.java)

**安卓端默认关闭池化功能**

[示例代码#createByteBufInAndroidTest](netty_demo/src/main/test/top/ersut/netty/ByteBufTest.java)

#### 3.5.4 结构

ByteBuf由4部分组成

![](images/0010.png)

##### ByteBuf重要的4个属性

- readIndex：读索引，最开始值是0
- writeIndex：写索引，最开始值是0
- capacity：目前容量，默认10
- maxCapcity：最大容量

#### 3.5.5 写入数据

```java
//创建byteBuf
ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

//写入数据
buffer.writeCharSequence("写入数据",StandardCharsets.UTF_8);
```

**读取数据**

```java
//获取写索引、读索引
int i = buffer.writerIndex();
int i1 = buffer.readerIndex()

//读取数据
byte[] read = new byte[i-i1];
buffer.readBytes(read);

String str = new String(read, StandardCharsets.UTF_8);
```

#### 



#### 3.5.3 影响创建的属性

##### ByteBuf创建的位置



##### ByteBufAllocator.DEFAULT.buffer()默认创建的类型
