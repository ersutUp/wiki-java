package top.ersut.reactive;

import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class ReactorDemo {

    public static void main(String[] args) {
        //使用Flux声明元素
        Flux.just("测试","test",123);

        //使用Mono声明元素
        Mono.just(234);

        //使用Flux声明元素并订阅
        Flux.just("测试","test",123).subscribe(System.out::println);

        System.out.println();

        //使用Mono声明元素并订阅
        Mono.just("list").subscribe(System.out::println);

    }

}
