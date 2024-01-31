package top.ersut.protocol.chat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.GroupCreateRequestMessage;
import top.ersut.protocol.chat.message.GroupCreateResponseMessage;
import top.ersut.protocol.chat.server.entity.User;
import top.ersut.protocol.chat.server.session.Group;
import top.ersut.protocol.chat.server.session.GroupSession;
import top.ersut.protocol.chat.server.session.GroupSessionFactory;
import top.ersut.protocol.chat.server.session.SessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        //添加自己
        User user = SessionFactory.getSession().getUser(ctx.channel());
        members.add(user.getUserName());

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        //创建群
        Group group = groupSession.createGroup(groupName, members);

        if (Objects.isNull(group)) {
            //群存在
            ctx.writeAndFlush(new GroupCreateResponseMessage(false,groupName+"已存在"));
        } else {
            //发送建群成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true,groupName+"创建成功"));
            //给成员发送消息
            List<Channel> membersChannel = groupSession.getMembersChannel(groupName);
            membersChannel.forEach(channel -> {
                channel.writeAndFlush(new GroupCreateResponseMessage(true,"您已被拉入："+groupName));
            });
        }
    }
}
