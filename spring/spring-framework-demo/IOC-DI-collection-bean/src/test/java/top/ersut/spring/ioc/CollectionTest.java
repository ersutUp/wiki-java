package top.ersut.spring.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class CollectionTest {

    @Test
    void print() {
        //创建上下文对象
        ApplicationContext applicationContext = new GenericXmlApplicationContext("bean.xml");
        //获取bean
        CollectionBean collection = applicationContext.getBean("collection",CollectionBean.class);
        //调用方法
        collection.print();
    }
}