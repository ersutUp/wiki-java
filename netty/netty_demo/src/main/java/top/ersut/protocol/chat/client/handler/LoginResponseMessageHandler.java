package top.ersut.protocol.chat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.message.LoginResponseMessage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class LoginResponseMessageHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {

    private final AtomicBoolean IS_LOGIN;
    private final CountDownLatch CONSOLE_IN_WAIT;

    public LoginResponseMessageHandler(final AtomicBoolean IS_LOGIN,final CountDownLatch CONSOLE_IN_WAIT){
        this.IS_LOGIN = IS_LOGIN;
        this.CONSOLE_IN_WAIT = CONSOLE_IN_WAIT;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage loginResponseMessage) throws Exception {
        log.info(loginResponseMessage.getReason());
        if (loginResponseMessage.isSuccess()) {
            IS_LOGIN.set(true);
        }
        CONSOLE_IN_WAIT.countDown();
    }
}