package top.ersut.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.collection.ByteCollections;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ByteBufTest {

    @Test
    public void createByteBufNotAndroidTest() {
        //非安卓情况下
        createByteBufTest();
    }
    @Test
    public void createByteBufInAndroidTest() {
        //模拟安卓环境
        System.setProperty("java.vm.name","Dalvik");
        createByteBufTest();
    }

    public void createByteBufTest(){
        /**
         * ByteBuf创建的位置：堆内存（heapBuffer） & 直接内存（directBuffer）
         * 1、直接内存中创建：在非andiron的环境下，并且不指定 -Dio.netty.noPreferDirect
         * 2、堆内存中创建，满足以下任意条件：
         *      - 在andiron的环境下
         *      - 指定 -Dio.netty.noPreferDirect=true
         *
         * ByteBuf创建是否启用池化，池化&非池化
         * 1、池化： 在非andiron的环境下，并且不指定 -Dio.netty.allocator.type
         * 2、非池化，满足以下任意条件：
         *      - 在andiron的环境下
         *      - 在非andiron的环境下，指定 -Dio.netty.allocator.type=unpooled
         */
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        log.info("buffer(),default class:[{}]",buffer);

//        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer();

//        ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer();

    }




}
