package top.ersut.protocol.chat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.GroupMembersRequestMessage;
import top.ersut.protocol.chat.message.GroupMembersResponseMessage;
import top.ersut.protocol.chat.message.GroupQuitRequestMessage;
import top.ersut.protocol.chat.message.GroupQuitResponseMessage;
import top.ersut.protocol.chat.server.session.Group;
import top.ersut.protocol.chat.server.session.GroupSession;
import top.ersut.protocol.chat.server.session.GroupSessionFactory;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.removeMember(msg.getGroupName(), msg.getUsername());

        if (group == null){
        //群不存在
            ctx.writeAndFlush(new GroupQuitResponseMessage(false,"群不存在："+msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true,"已退出群聊："+msg.getGroupName()));
        }

    }
}
