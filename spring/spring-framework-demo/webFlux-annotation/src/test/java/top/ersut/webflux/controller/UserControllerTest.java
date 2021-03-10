package top.ersut.webflux.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import top.ersut.webflux.pojo.User;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

//SpringBootTest.WebEnvironment.DEFINED_PORT : 启动真实web环境
//properties = {"server.port=8082"} 设置启动端口
@SpringBootTest(properties = {"server.port=8082"},webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {

    @Value("${server.port:8080}")
    private String port;

    WebClient webClient;
    @PostConstruct
    public void init(){
        webClient = WebClient.create("http://127.0.0.1:"+port);
    }

    @Test
    void queryByID() {
        //get请求
        User user = webClient.get()
                //请求地址
                .uri("/user/{id}",1)
                //设置Accept请求头
                .accept(MediaType.APPLICATION_JSON)
                //发起请求并接受响应
                .retrieve()
                //响应体转换实体
                .bodyToMono(User.class)
                //获取实例
                .block();
        Assertions.assertNotNull(user);
    }

    @Test
    void queryAll() {
        List<User> users = new ArrayList();
        webClient.get()
                .uri("/user")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(User.class)
                //关键
                .buffer().doOnNext(e->{users.addAll(e);})
                .blockFirst();
        Assertions.assertNotEquals(users.size(),0);
    }

    @Test
    void saveUser() {
        User user = new User("test");
        User userResult = webClient.post()
                .uri("/user")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class)
                .block();
        Assertions.assertNotNull(userResult.getID());
        Assertions.assertEquals(user.getName(),userResult.getName());
    }

    @Test
    void savesUser() {
        List<User> userList = new ArrayList<User>(){
            {
                add(new User("test"));
                add(new User("test1"));
            }
        };

        List<User> userListResult = new ArrayList<>();
        webClient.post()
                .uri("/user/batch")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userList)
                .retrieve()
                .bodyToFlux(User.class)
                .buffer().doOnNext(user -> {userListResult.addAll(user);})
                .blockFirst();
        Assertions.assertEquals(userList.size(),userListResult.size());
        Assertions.assertNotNull(userListResult.get(0).getID());
        Assertions.assertNotNull(userListResult.get(1).getID());
    }
}