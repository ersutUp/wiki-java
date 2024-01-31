package top.ersut.protocol.chat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.ersut.protocol.chat.message.GroupChatRequestMessage;
import top.ersut.protocol.chat.message.GroupChatResponseMessage;
import top.ersut.protocol.chat.message.GroupJoinRequestMessage;
import top.ersut.protocol.chat.server.session.GroupSession;
import top.ersut.protocol.chat.server.session.GroupSessionFactory;

import java.util.List;
import java.util.Objects;

@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        List<Channel> membersChannel = groupSession.getMembersChannel(msg.getGroupName());
        //发送消息，排除自己
        membersChannel.stream().filter(channel -> !Objects.equals(channel,ctx.channel())).forEach(channel -> {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        });

    }
}
