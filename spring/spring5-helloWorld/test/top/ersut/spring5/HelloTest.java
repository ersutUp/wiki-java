package top.ersut.spring5;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

class HelloTest {
    @Test
    void world() {
        //创建上下文对象
        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
        //获取bean
        Hello hello = applicationContext.getBean("hello",Hello.class);
        //调用方法
        hello.world();
    }
}