package xyz.ersut.service.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import xyz.ersut.common.annotation.EnableProjectFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableProjectFeignClients
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
