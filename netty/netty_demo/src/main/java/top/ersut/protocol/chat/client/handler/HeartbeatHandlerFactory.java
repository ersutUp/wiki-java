package top.ersut.protocol.chat.client.handler;

import com.sun.org.apache.bcel.internal.generic.PUSH;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.message.PingMessage;

@Slf4j
public class HeartbeatHandlerFactory {

    PingMessageHandler pingMessageHandler = new PingMessageHandler();

    public IdleStateHandler getIdleStateHandler(){
        return new IdleStateHandler(0,8,0);
    }

    public PingMessageHandler getPingMessageHandler(){
        return pingMessageHandler;
    }


    @ChannelHandler.Sharable
    class PingMessageHandler extends ChannelDuplexHandler {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleStateEvent = ((IdleStateEvent) evt);
                //IdleStateHandler的读事件
                if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                    log.info("发送一个心跳");
                    ctx.writeAndFlush(new PingMessage());
                }
            }
        }
    }



}
