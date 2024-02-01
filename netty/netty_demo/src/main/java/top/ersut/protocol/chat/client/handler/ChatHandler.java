package top.ersut.protocol.chat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.ersut.protocol.chat.message.*;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class ChatHandler extends ChannelInboundHandlerAdapter {

    //用户在控制台输入等待
    private final CountDownLatch CONSOLE_IN_WAIT;
    //是否登录
    private AtomicBoolean IS_LOGIN;
    //是否退出
    private AtomicBoolean IS_EXIT;
    public ChatHandler(CountDownLatch CONSOLE_IN_WAIT,AtomicBoolean IS_LOGIN,AtomicBoolean IS_EXIT){
        this.CONSOLE_IN_WAIT = CONSOLE_IN_WAIT;
        this.IS_LOGIN = IS_LOGIN;
        this.IS_EXIT = IS_EXIT;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("与服务器断开了连接");
        IS_EXIT.set(true);
        System.out.println("与服务器断开了连接按换行键退出......");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.info("与服务器异常断开了连接");
        IS_EXIT.set(true);
        System.out.println("与服务器断开了连接按换行键退出......");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.print("请输入用户名：");
            String username = scanner.nextLine();
            if(IS_EXIT.get()){
                return;
            }

            System.out.print("请输入密码：");
            String password = scanner.nextLine();
            if(IS_EXIT.get()){
                return;
            }
            LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
            ctx.writeAndFlush(loginRequestMessage);

            System.out.println("等待响应......");
            try {
                CONSOLE_IN_WAIT.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (IS_LOGIN.get()) {
                System.out.println("登录成功");
            } else {
                System.out.println("登录失败");
                ctx.channel().close();
                return;
            }

            while (true) {
                if(IS_EXIT.get()){
                    return;
                }
                System.out.println("============ 功能菜单 ============\n"
                        + "send [username] [content]\n"
                        + "gsend [group name] [content]\n"
                        + "gcreate [group name] [m1,m2,m3...]\n"
                        + "gmembers [group name]\n"
                        + "gjoin [group name]\n"
                        + "gquit [group name]\n"
                        + "quit\n"
                        + "==================================");
                //等待用户输入，并阻塞线程
                String command = scanner.nextLine();
                String[] params = command.split(" ");
                switch (params[0]) {
                    case "send":
                        ctx.writeAndFlush(new ChatRequestMessage(params[1],params[2]));
                        break;
                    case "gsend":
                        ctx.writeAndFlush(new GroupChatRequestMessage(username,params[1],params[2]));
                        break;
                    case "gcreate":
                        //组成员列表
                        Set<String> members = Arrays.stream(params[2].split(",")).collect(Collectors.toSet());
                        ctx.writeAndFlush(new GroupCreateRequestMessage(params[1],members));
                        break;
                    case "gmembers":
                        ctx.writeAndFlush(new GroupMembersRequestMessage(params[1]));
                        break;
                    case "gjoin":
                        ctx.writeAndFlush(new GroupJoinRequestMessage(username,params[1]));
                        break;
                    case "gquit":
                        ctx.writeAndFlush(new GroupQuitRequestMessage(username,params[1]));
                        break;
                    case "quit":
                        ctx.channel().close();
                        //线程结束
                        return;
                }
            }
        }, "system in").start();
    }
}
