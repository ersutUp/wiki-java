package top.ersut.protocol.chat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.LoginRequestMessage;
import top.ersut.protocol.chat.message.LoginResponseMessage;
import top.ersut.protocol.chat.server.entity.User;
import top.ersut.protocol.chat.server.service.UserService;
import top.ersut.protocol.chat.server.service.UserServiceFactory;
import top.ersut.protocol.chat.server.session.SessionFactory;

@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        LoginResponseMessage loginResponseMessage;

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.login(msg.getAccount(), msg.getPassword());
        if (user != null) {
            //放入session
            SessionFactory.getSession().bind(ctx.channel(),user);
            loginResponseMessage = new LoginResponseMessage(true, "登录成功");
        } else {
            loginResponseMessage = new LoginResponseMessage(false, "登录成功");
        }

        ctx.writeAndFlush(loginResponseMessage);
    }
}
