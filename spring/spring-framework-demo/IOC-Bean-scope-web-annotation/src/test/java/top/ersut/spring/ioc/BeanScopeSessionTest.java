package top.ersut.spring.ioc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@SpringBootTest(properties = {"server.port=8082"},webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BeanScopeSessionTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${server.port:8080}")
    private String port;

    WebClient webClient;
    @PostConstruct
    public void init(){
        webClient = WebClient.create("http://127.0.0.1:"+port);
    }

    @Test
    public void session(){
        //同步cookie
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();

        Integer num = webClient.get()
                //请求地址
                .uri("/session/{count}",1)
                //设置Accept请求头
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(r -> {
                    r.cookies().keySet().forEach(e -> {
                        cookies.add(e,r.cookies().get(e).get(0).getValue());
                    });
                    return r.bodyToMono(Integer.class);
                })
                .block();


        Integer num2 = webClient.get()
                //请求地址
                .uri("/session/{count}",2)
                //设置Accept请求头
                .accept(MediaType.APPLICATION_JSON)
                .cookies(m -> m.addAll(cookies))
                //发起请求并接受响应
                .retrieve()
                .bodyToMono(Integer.class)
                .block();

        logger.info("num:"+num);
        logger.info("num2:"+num2);

        Assertions.assertEquals(num,num2);
    }




}