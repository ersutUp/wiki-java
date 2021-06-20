package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"server.port=8082"},webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BeanScopeRequestTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.port:8080}")
    private String port;

    WebClient webClient;
    @PostConstruct
    public void init(){
        webClient = WebClient.create("http://127.0.0.1:"+port);
    }

    @Test
    public void request(){
        String str = webClient.get()
                //请求地址
                .uri("/request")
                //设置Accept请求头
                .accept(MediaType.APPLICATION_JSON)
                //发起请求并接受响应
                .retrieve()
                .bodyToMono(String.class)
                .block();


        String str2 = webClient.get()
                //请求地址
                .uri("/request")
                //设置Accept请求头
                .accept(MediaType.APPLICATION_JSON)
                //发起请求并接受响应
                .retrieve()
                .bodyToMono(String.class)
                .block();

        logger.info("str:"+str);
        logger.info("str2:"+str2);

        Assertions.assertNotEquals(str,str2);

    }




}