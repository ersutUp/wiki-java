package top.ersut.protocol.chat.server.session;

import io.netty.channel.Channel;
import top.ersut.protocol.chat.server.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session {

    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, User> channelUserMap = new ConcurrentHashMap<>();
    private final Map<Channel,Map<String,Object>> channelAttributesMap = new ConcurrentHashMap<>();// 每个 channel 包含的属性

    @Override
    public void bind(Channel channel, User user) {
        usernameChannelMap.put(user.getUserName(), channel);
        channelUserMap.put(channel, user);
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
    }

    @Override
    public void unbind(Channel channel) {
        User user = channelUserMap.remove(channel);
        usernameChannelMap.remove(user.getUserName());
        channelAttributesMap.remove(channel);
    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return channelAttributesMap.get(channel).get(name);
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        channelAttributesMap.get(channel).put(name, value);
    }

    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public User getUser(Channel channel) {
        return channelUserMap.get(channel);
    }

    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }
}
