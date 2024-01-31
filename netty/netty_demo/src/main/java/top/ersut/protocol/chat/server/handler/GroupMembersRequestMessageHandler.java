package top.ersut.protocol.chat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.GroupJoinRequestMessage;
import top.ersut.protocol.chat.message.GroupJoinResponseMessage;
import top.ersut.protocol.chat.message.GroupMembersRequestMessage;
import top.ersut.protocol.chat.message.GroupMembersResponseMessage;
import top.ersut.protocol.chat.server.session.Group;
import top.ersut.protocol.chat.server.session.GroupSession;
import top.ersut.protocol.chat.server.session.GroupSessionFactory;

import java.util.Set;

@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(msg.getGroupName());

        if(members.isEmpty()){
            //群不存在
            ctx.writeAndFlush(new GroupMembersResponseMessage(false,"群不存在："+msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupMembersResponseMessage(members));
        }

    }
}
