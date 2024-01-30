package top.ersut.protocol.chat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.ChatRequestMessage;
import top.ersut.protocol.chat.message.ChatResponseMessage;
import top.ersut.protocol.chat.message.LoginRequestMessage;
import top.ersut.protocol.chat.message.LoginResponseMessage;
import top.ersut.protocol.chat.server.entity.User;
import top.ersut.protocol.chat.server.service.UserService;
import top.ersut.protocol.chat.server.service.UserServiceFactory;
import top.ersut.protocol.chat.server.session.Session;
import top.ersut.protocol.chat.server.session.SessionFactory;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String toUserName = msg.getTo();
        Channel toChannel = SessionFactory.getSession().getChannel(toUserName);

        ChatResponseMessage chatResponseMessage;

        if (toChannel == null) {
            chatResponseMessage = new ChatResponseMessage(false,"用户不在线或不存在");
            ctx.writeAndFlush(chatResponseMessage);
        } else {
            chatResponseMessage = new ChatResponseMessage(SessionFactory.getSession().getUser(ctx.channel()).getUserName(), msg.getContent());
            toChannel.writeAndFlush(chatResponseMessage);
        }


    }
}
