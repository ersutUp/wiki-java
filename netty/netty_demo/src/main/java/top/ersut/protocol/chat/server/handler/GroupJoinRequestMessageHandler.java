package top.ersut.protocol.chat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.GroupCreateRequestMessage;
import top.ersut.protocol.chat.message.GroupCreateResponseMessage;
import top.ersut.protocol.chat.message.GroupJoinRequestMessage;
import top.ersut.protocol.chat.message.GroupJoinResponseMessage;
import top.ersut.protocol.chat.server.session.Group;
import top.ersut.protocol.chat.server.session.GroupSession;
import top.ersut.protocol.chat.server.session.GroupSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        String groupName = msg.getGroupName();
        Group group = groupSession.joinMember(groupName, msg.getUsername());
        if(group == null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(false,"群不存在："+msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true,"加入成功："+msg.getGroupName()));
        }

    }
}
