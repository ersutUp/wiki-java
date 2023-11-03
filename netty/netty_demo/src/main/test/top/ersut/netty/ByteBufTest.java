package top.ersut.netty;

import com.sun.xml.internal.stream.util.BufferAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.DuplicatedByteBuf;
import io.netty.util.collection.ByteCollections;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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


    @Test
    public void witerAndReadTest() {
        //创建byteBuf
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        //写入数据
        buffer.writeCharSequence("写入数据",StandardCharsets.UTF_8);

        //获取写索引、读索引
        int i = buffer.writerIndex();
        int i1 = buffer.readerIndex();

        //读取数据
        byte[] read = new byte[i-i1];
        buffer.readBytes(read);

        String str = new String(read, StandardCharsets.UTF_8);
        log.info(str);
    }

    @Test
    public void markTest() {
        //创建byteBuf
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        log.info(buffer.writerIndex()+","+buffer.readerIndex());
        //写入数据
        buffer.writeCharSequence("abcd",StandardCharsets.UTF_8);

        CharSequence charSequence = buffer.readCharSequence(1, StandardCharsets.UTF_8);
        log.info("读取第一个字节："+charSequence.toString());

        //标记读索引
        buffer.markReaderIndex();
        for (int i = 0; i < 5; i++) {
            //重置读索引到标记位
            buffer.resetReaderIndex();
            log.info("读取第二个字节："+buffer.readCharSequence(1, StandardCharsets.UTF_8));
        }
        log.info("读取第三四个字节："+buffer.readCharSequence(2, StandardCharsets.UTF_8));
    }

    @Test
    public void sliceTest(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j',});
        buffer.markReaderIndex();
        log.info("原始（buffer）的容量：[{}]，值：[{}]", buffer.capacity(), buffer.readCharSequence(buffer.capacity(), StandardCharsets.UTF_8));

        //将前五个进行切片
        //参数1：切片的起始位置；参数2：切片的数量
        log.info("--------------将前五个进行切片----------------");
        ByteBuf buffer1 = buffer.slice(0, 5);
        buffer1.markReaderIndex();
        log.info("切片（buffer1）的容量：[{}]，值：[{}]", buffer1.capacity(), buffer1.readCharSequence(buffer1.capacity(), StandardCharsets.UTF_8));

        //将后五个进行切片
        log.info("--------------将后五个进行切片----------------");
        ByteBuf buffer2 = buffer.slice(5, 5);
        buffer2.markReaderIndex();
        log.info("切片（buffer2）的容量：[{}]，值：[{}]", buffer2.capacity(), buffer2.readCharSequence(buffer2.capacity(), StandardCharsets.UTF_8));

        /**
         * 注意事项:
         *      1、由于切片的ByteBuf与原始的ByteBuf使用的同一块内存，所以写入切片的ByteBuf时原始ByteBuf也会变。
         *      2、切片后的ByteBuf会有大小限制，切片时多大就是多大，超出后报错。
         */
        //注意事项1
        log.info("--------------注意事项1----------------");

        buffer.resetReaderIndex();
        log.info("原始（buffer）的容量：[{}]，值：[{}]", buffer.capacity(), buffer.readCharSequence(buffer.capacity(), StandardCharsets.UTF_8));

        buffer2.setByte(0,'y');
        buffer2.resetReaderIndex();
        log.info("切片（buffer2）的容量：[{}]，值：[{}]", buffer2.capacity(), buffer2.readCharSequence(buffer2.capacity(), StandardCharsets.UTF_8));

        buffer.resetReaderIndex();
        log.info("原始（buffer）的容量：[{}]，值：[{}]", buffer.capacity(), buffer.readCharSequence(buffer.capacity(), StandardCharsets.UTF_8));

        //注意事项2
        log.info("--------------注意事项2----------------");
        try {
            buffer1.setByte(5,'x');
        }catch (IndexOutOfBoundsException e){
            log.error("索引超出",e);
        }
    }

    //软拷贝
    @Test
    public void duplicateTest(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j',});
        buffer.markReaderIndex();
        log.info("原始（buffer）的容量：[{}]，值：[{}]", buffer.capacity(), buffer.readCharSequence(buffer.capacity(), StandardCharsets.UTF_8));


        /**
         * 软拷贝：
         *      与原始buffer使用同一块内存，并且可以进行扩容
         *      读写指针是单独的，与原始buffer互不影响
         *          拷贝时会把原始Buffer读写指针的值拷贝过来
         */
        ByteBuf duplicateBuffer = buffer.duplicate();

        //创建软拷贝对象是会把原始Buffer读写指针的值拷贝过来
        log.info("--------------读写指针的拷贝----------------");
        log.info("原始（buffer）的读指针：[{}]，写指针：[{}]", buffer.readerIndex(),buffer.writerIndex());
        log.info("软拷贝（duplicateBuffer）的读指针：[{}]，写指针：[{}]", duplicateBuffer.readerIndex(),duplicateBuffer.writerIndex());

        //读写指针是单独的，与原始buffer互不影响
        log.info("---------与原始Buffer的指针互不影响----------------");
        duplicateBuffer.readerIndex(0);
        duplicateBuffer.markReaderIndex();
        log.info("原始（buffer）的读指针：[{}]，写指针：[{}]", buffer.readerIndex(),buffer.writerIndex());
        log.info("软拷贝（duplicateBuffer）的读指针：[{}]，写指针：[{}]", duplicateBuffer.readerIndex(),duplicateBuffer.writerIndex());

        buffer.readerIndex(5);
        log.info("原始（buffer）的读指针：[{}]，写指针：[{}]", buffer.readerIndex(),buffer.writerIndex());
        log.info("软拷贝（duplicateBuffer）的读指针：[{}]，写指针：[{}]", duplicateBuffer.readerIndex(),duplicateBuffer.writerIndex());


        log.info("软拷贝（duplicateBuffer）的容量：[{}]，值：[{}]", duplicateBuffer.capacity(), duplicateBuffer.readCharSequence(duplicateBuffer.capacity(), StandardCharsets.UTF_8));

        buffer.resetReaderIndex();
        duplicateBuffer.resetReaderIndex();
        log.info("---------软拷贝中进行扩容----------------");
        duplicateBuffer.writeByte('k');
        log.info("原始（buffer）的容量：[{}]，值：[{}]", buffer.capacity(), buffer.readCharSequence(buffer.capacity(), StandardCharsets.UTF_8));
        log.info("软拷贝（duplicateBuffer）的容量：[{}]，值：[{}]", duplicateBuffer.capacity(), duplicateBuffer.readCharSequence(duplicateBuffer.capacity(), StandardCharsets.UTF_8));
        log.info("原始（buffer）的读指针：[{}]，写指针：[{}]", buffer.readerIndex(),buffer.writerIndex());
        log.info("软拷贝（duplicateBuffer）的读指针：[{}]，写指针：[{}]", duplicateBuffer.readerIndex(),duplicateBuffer.writerIndex());

        buffer.resetReaderIndex();
        duplicateBuffer.resetReaderIndex();
        //与原始buffer使用同一块内存，并且可以进行扩容
        log.info("---------与原始Buffer使用同一块缓存----------------");
        buffer.setByte(0,'x');
        duplicateBuffer.setByte(1,'y');
        log.info("原始（buffer）的容量：[{}]，值：[{}]", buffer.capacity(), buffer.readCharSequence(buffer.capacity(), StandardCharsets.UTF_8));
        log.info("软拷贝（duplicateBuffer）的容量：[{}]，值：[{}]", duplicateBuffer.capacity(), duplicateBuffer.readCharSequence(duplicateBuffer.capacity(), StandardCharsets.UTF_8));
    }

    @Test
    public void compositeByteBufTest(){
        ByteBuf buffer1 = ByteBufAllocator.DEFAULT.buffer(5);
        buffer1.writeBytes(new byte[]{'a','b','c','d'});
        log.info("buffer1的容量：[{}]，值：[{}]", buffer1.capacity(), buffer1.getCharSequence(0,buffer1.writerIndex(), StandardCharsets.UTF_8));

        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.buffer(5);
        buffer2.writeBytes(new byte[]{'f','g','h','i','j'});
        log.info("buffer2的容量：[{}]，值：[{}]", buffer2.capacity(), buffer2.getCharSequence(0,buffer2.writerIndex(), StandardCharsets.UTF_8));

        buffer1.readerIndex(1);

        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //整合ByteBuf
        compositeByteBuf.addComponents(true,buffer1,buffer2);
        log.info("compositeByteBuf的容量：[{}]，值：[{}]", compositeByteBuf.capacity(), compositeByteBuf.getCharSequence(0,compositeByteBuf.capacity(), StandardCharsets.UTF_8));
        log.info("compositeByteBuf的读指针：[{}]，写指针：[{}]", compositeByteBuf.readerIndex(),compositeByteBuf.writerIndex());


        compositeByteBuf.setByte(1,'x');
        log.info("compositeByteBuf的容量：[{}]，值：[{}]", compositeByteBuf.capacity(), compositeByteBuf.getCharSequence(0,compositeByteBuf.capacity(), StandardCharsets.UTF_8));
        log.info("buffer1的容量：[{}]，值：[{}]", buffer1.capacity(), buffer1.getCharSequence(0,buffer1.writerIndex(), StandardCharsets.UTF_8));

    }


}
