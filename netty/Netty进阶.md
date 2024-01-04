# Netty进阶



## 一、粘包与半包

需求：客户端发送5条"0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09"到服务端，服务端可正确解析每一条。

### 1.1 粘包现象&半包现象

**客户端发送数据部分代码：**

```java

...

log.info("发送数据");
for (int i = 0; i < 5; i++) {
    ByteBuf byteBuf = ctx.alloc().buffer();
    //每条信息10个字节
    byteBuf.writeBytes(new byte[]{0,1, 2, 3, 4, 5, 6, 7,8,9});
    ctx.writeAndFlush(byteBuf);
}
ctx.channel().close();

...

```

客户端一次性发出来5条数据出去。

**服务端部分代码：**

```java

...

ServerBootstrap serverBootstrap = new ServerBootstrap();
serverBootstrap.channel(NioServerSocketChannel.class)
//NioSocketChannel 中ByteBuf 大小限制 16个字节
.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))
.group(boos, works)
.childHandler(new ChannelInitializer<NioSocketChannel>() {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = ((ByteBuf) msg);
                log.info("byteBuf.writerIndex():[{}]",byteBuf.writerIndex());
                log.info("byteBuf.capacity():[{}]",byteBuf.capacity());
                super.channelRead(ctx, msg);
            }
        });
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
    }
});

...

```

其中 `.childOption(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator(16,16,16))`是控制服务端接收数据的ByteBuf大小，目前设置为16字节。

**服务端控制台打印：**

![](./images/StickHalfPackageLog.png)

[相关代码](./netty_demo/src/main/test/top/ersut/netty/stickhalfpackage/StickHalfPackageTest.java)

### 1.2 解决方案 

#### 1.2.1 通过换行符解决粘包半包问题

 [相关代码](netty_demo/src/main/test/top/ersut/netty/stickhalfpackage/SolutionByLineTest.java)

**根据定界符：换行(\n or \r\n) ，识别一条完整的消息**



**服务端通过 `LineBasedFrameDecoder`解码消息**

- 构造方法：`public LineBasedFrameDecoder(final int maxLength)`
  - maxLength：限制一条消息最大的长度，如果超出抛出`TooLongFrameException `异常

**客户端通过`LineEncoder`编码消息；**



服务端部分代码：

```java
NioEventLoopGroup boss = new NioEventLoopGroup();
NioEventLoopGroup works = new NioEventLoopGroup();
try {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boss, works);
    serverBootstrap.channel(NioServerSocketChannel.class);
    serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            //根据 换行符 进行解码
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
    });
    ChannelFuture channelFuture = serverBootstrap.bind(PROT);

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channelFuture.channel().closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    boss.shutdownGracefully();
    works.shutdownGracefully();
}
```



客户端代码：

```java
NioEventLoopGroup work = new NioEventLoopGroup();
try {
    ChannelFuture channelFuture = new Bootstrap()
            .group(work)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //在每条消息最后添加 换行符 的编码器
                    ch.pipeline().addLast(new LineEncoder());
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.info("发送数据");
                            for (int i = 0; i < 5; i++) {
                                //这里要写入 CharSequence类型， 因为LineEncoder只处理 CharSequence类型 的数据
                                ctx.writeAndFlush(new String(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}));
                            }
                            ctx.channel().close();
                        }
                    });
                }
            })
            .connect(new InetSocketAddress(PROT));
    Channel channel = channelFuture.channel();

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channel.closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    work.shutdownGracefully();
}
```



客户端发出后，服务端的打印:

![](./images/LineDecode-log.png)



#### 1.2.2 通过长度字段解决粘包半包问题

[相关代码](netty_demo/src/main/test/top/ersut/netty/stickhalfpackage/SolutionByLengthFieldTest.java) 

**根据长度字段的解析方法**



获取到**长度字段的值**后，**按照长度值的大小，向后读取字节个数**，这些字节就是消息的内容。

例如，接收到的消息为：**0x05**,0x01,0x02,0x03,0x04,0x05,**0x03**,0x06,0x07,0x08

**0x05**为长度字段，那向后继续读取5个字节，则是消息的内容，即0x01,0x02,0x03,0x04,0x05

该消息完整解析如下：

| 长度 | 消息的内容               |
| ---- | ------------------------ |
| 0x05 | 0x01,0x02,0x03,0x04,0x05 |
| 0x03 | 0x06,0x07,0x08           |

这个示例是**长度字段表示消息内容的长度**，另一种方式是**长度字段表示整体消息（包含长度字段）的长度**，就不写了



**netty提供了`LengthFieldBasedFrameDecoder`类实现了长度字段解析消息**

`LengthFieldBasedFrameDecoder`对消息分成了两部分

- **头部分：包含了长度字段，另外允许添加一些其他字段**
- 内容部分：真实的消息

`LengthFieldBasedFrameDecoder`的构造方法：

```java
public LengthFieldBasedFrameDecoder(
        int maxFrameLength,
        int lengthFieldOffset, int lengthFieldLength,
        int lengthAdjustment, int initialBytesToStrip)
```

- maxFrameLength：限制消息的最大长度
- lengthFieldOffset：长度字段在消息中的偏移量，长度字段前有附加的头信息造成的。
- lengthFieldLength：长度字段占用的字节个数
- lengthAdjustment：长度字段后的头信息还有多少个字节，
- initialBytesToStrip：从整个消息中剥离出去多少个字节（从头部开始剥离），0则不剥离



**netty提供了`LengthFieldPrepender`类实现了长度字段编码消息**

构造方法

```
public LengthFieldPrepender(int lengthFieldLength, int lengthAdjustment)
```

- lengthFieldLength：长度字段占用的字节数
- lengthAdjustment：附加的头信息占用的字节（需要是负数）

**服务端示例代码**：

```java
NioEventLoopGroup boss = new NioEventLoopGroup();
NioEventLoopGroup works = new NioEventLoopGroup();
try {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(boss, works);
    serverBootstrap.channel(NioServerSocketChannel.class);
    serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            //长度字段的值是消息内容的长度
            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 3, 2)); //1处
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
    });
    ChannelFuture channelFuture = serverBootstrap.bind(PROT);

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channelFuture.channel().closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    boss.shutdownGracefully();
    works.shutdownGracefully();
}
```

1处 是 长度字段编码器，指出**消息最大为1024**，**长度字段是第一个和第二个字节**，并且长度字段后有**三个字节为附加头**，**剥离掉两个字节（即长度字段）**。



**客户端示例代码**：

```java
NioEventLoopGroup work = new NioEventLoopGroup();
try {
    ChannelFuture channelFuture = new Bootstrap()
            .group(work)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    //长度字段 编码器
                    ch.pipeline().addLast(new LengthFieldPrepender(2, -3));//2处
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.info("发送数据");
                            for (int i = 0; i < 5; i++) {
                                ByteBuf buffer = ctx.alloc().buffer();
                                //自定义的头信息
                                byte[] header = new byte[]{(byte) 0xff, (byte) 0xaa};
                                buffer.writeBytes(header);
                                //数据
                                byte[] data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                                buffer.writeBytes(data);

                                ctx.writeAndFlush(buffer);
                            }
                            ctx.channel().close();
                        }
                    });
                }
            })
            .connect(new InetSocketAddress(PROT));
    Channel channel = channelFuture.channel();

    channelFuture.addListener(future -> {
        if (future.isSuccess()) {
            log.info("启动成功");
        } else {
            log.error("启动失败", future.cause());
        }
    });
    channel.closeFuture().sync();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
} finally {
    work.shutdownGracefully();
}
```

2处是长度字段编码器，指出长度字段占用了2个字节，并且还有3个字节为附加的头信息



**服务端控制台的打印**：

![](./images/LengthFieldDecode-log.png)
