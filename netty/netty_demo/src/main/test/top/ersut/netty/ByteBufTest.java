package top.ersut.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.collection.ByteCollections;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

@Slf4j
public class ByteBufTest {
    @Setter
    ByteBuf heapBuffer;
    @Setter
    ByteBuf directBuffer;
    @Setter
    ByteBuf buffer;

    public void createByteBufTest() {
        /**
         * ByteBuf创建的位置
         *      - 在堆中创建（heapBuffer）
         *          - 优点：创建速度快
         *          - 缺点：效率低，创建在堆内存中使用时需要复制到主存中，另外受GC影响会进行复制等
         *      - 在内存中直接创建（directBuffer）
         *          - 优点：效率高，由于在主存中直接创建，省去了堆内存复制到主存的步骤
         *          - 缺点：创建速度慢
         */
        //创建在堆内存
        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
        log.info("heapBuffer(),heapBuffer class:[{}]",heapBuffer);
        setHeapBuffer(heapBuffer);
        //创建在主存
        ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer();
        log.info("directBuffer(),directBuffer class:[{}]",directBuffer);
        setDirectBuffer(directBuffer);
        /**
         * buffer()的一些说明
         *
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
        setBuffer(buffer);
    }

    @Test
    public void createByteBufDefaultTest(){
        createByteBufTest();
        Assert.assertEquals(buffer.getClass().getTypeName(),directBuffer.getClass().getTypeName());
    }

    @Test
    public void createByteBufUnpooledTest(){
        System.setProperty("io.netty.allocator.type","unpooled");
        createByteBufTest();
        Assert.assertEquals(buffer.getClass().getTypeName(),directBuffer.getClass().getTypeName());
    }

    @Test
    public void createByteBufNoPreferDirectTest(){
        System.setProperty("io.netty.noPreferDirect","true");
        createByteBufTest();
        Assert.assertEquals(buffer.getClass().getTypeName(),heapBuffer.getClass().getTypeName());
    }

    @Test
    public void createByteBufUnpooledNoPreferDirectTest(){
        System.setProperty("io.netty.noPreferDirect","true");
        createByteBufTest();
        Assert.assertEquals(buffer.getClass().getTypeName(),heapBuffer.getClass().getTypeName());
    }


    @Test
    public void createByteBufNotAndroidTest() {
        //非安卓情况下
        createDefaultByteBufTest();
    }
    @Test
    public void createByteBufInAndroidTest() {
        //模拟安卓环境
        System.setProperty("java.vm.name","Dalvik");
        createDefaultByteBufTest();
    }

    public void createDefaultByteBufTest(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        log.info("buffer(),default class:[{}]",buffer);
    }




}
