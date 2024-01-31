package top.ersut.protocol.chat.server.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.message.ChatRequestMessage;
import top.ersut.protocol.chat.message.ChatResponseMessage;
import top.ersut.protocol.chat.server.entity.User;
import top.ersut.protocol.chat.server.session.Session;
import top.ersut.protocol.chat.server.session.SessionFactory;

import java.util.Objects;

@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        clearClientData(ctx);
        super.channelInactive(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常断开");
        clearClientData(ctx);
        super.exceptionCaught(ctx, cause);
    }

    private static void clearClientData(ChannelHandlerContext ctx) {
        Session session = SessionFactory.getSession();
        Channel channel = ctx.channel();
        User user = session.getUser(channel);
        if (Objects.isNull(user)) {
            log.info("客户端断开");
        } else {
            log.info("[{}]客户端断开", user.getUserName());
            session.unbind(channel);
        }
    }
}
