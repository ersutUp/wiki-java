package top.ersut;

import io.netty.util.internal.StringUtil;
import org.junit.Test;
import top.ersut.utils.ByteBufferUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ByteBufferDemoTest {


    @Test
    //测试读取
    public void testRead(){
        try (RandomAccessFile file = new RandomAccessFile("abc.txt", "rw")) {
            FileChannel channel = file.getChannel();
            //创建一个 大小为11 的ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(11);
            while (true) {
                //从通道中读取数据到ByteBuffer，每次读取的数据是ByteBuffer的大小
                int len = channel.read(byteBuffer);
                //len表示读取了多少数据，0表示数据已全部读取
                if (len <= 0) {
                    break;
                }
                //切换为读模式
                byteBuffer.flip();
                for (int i = 0; i < len; i++) {
                    System.out.println((char) byteBuffer.get());
                }
                //切换为写模式
                byteBuffer.clear();
            }
        } catch (IOException ioException) {
        }
    }
    @Test
    //测试写入
    public void testWrite(){
        try (RandomAccessFile file = new RandomAccessFile("write.txt", "rw")) {
            FileChannel channel = file.getChannel();

            //创建ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            //往ByteBuffer中写入数据
            byteBuffer.put("test".getBytes());
            //切换为读模式
            byteBuffer.flip();

            //通过channel将ByteBuffer写入到文件中
            channel.write(byteBuffer);
        } catch (IOException ioException) {
        }
    }


    public ByteBuffer structure(){
        //1、创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        ByteBufferUtil.debugAll(byteBuffer);
        //2、往ByteBuffer中写入5个字节
        byteBuffer.put("abcde".getBytes());
        ByteBufferUtil.debugAll(byteBuffer);
        //3、切换为读模式
        byteBuffer.flip();
        ByteBufferUtil.debugAll(byteBuffer);

        //4、读取两个字节
        System.out.println((char)byteBuffer.get());
        System.out.println((char)byteBuffer.get());
        ByteBufferUtil.debugAll(byteBuffer);

        return byteBuffer;
    }

    /**
     * ByteBuffer结构示例：clear情况
     */
    @Test
    public void testStructureClear(){
        ByteBuffer byteBuffer = structure();

        //5、切换为写模式，情况1：执行clear()动作
        byteBuffer.clear();
        ByteBufferUtil.debugAll(byteBuffer);

    }

    /**
     * ByteBuffer结构示例：compact情况
     */
    @Test
    public void testStructureCompact() {
        ByteBuffer byteBuffer = structure();

        //5、切换为写模式，情况2：执行compact()动作，把未读取的数据向前压缩
        byteBuffer.compact();
        ByteBufferUtil.debugAll(byteBuffer);
    }


    /**
     * ByteBuffer写入数据
     */
    @Test
    public void testPut() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入单个字节，写入后 position 自动 +1
        byteBuffer.put((byte)'a');
        ByteBufferUtil.debugAll(byteBuffer);
        System.out.println();

        //写入多个字节，写入后 position 自动 + byte数组的长度
        byteBuffer.put(new byte[]{'b','c'});
        ByteBufferUtil.debugAll(byteBuffer);
        System.out.println();

        //指定位置写入字节，注意该方式 position 不会变动
        byteBuffer.put(2,(byte)'e');
        ByteBufferUtil.debugAll(byteBuffer);

        byteBuffer.clear();
        ByteBufferUtil.debugAll(byteBuffer);

    }

    /**
     * ByteBuffer清空数据
     */
    @Test
    public void testClear() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入多个字节
        byteBuffer.put(new byte[]{'a','b','c'});
        ByteBufferUtil.debugAll(byteBuffer);
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

        //清空数据，实际上是移动了postion和limit，并未真正的清除数据
        byteBuffer.clear();
        ByteBufferUtil.debugAll(byteBuffer);
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

        //覆盖之前的数据写入新数据
        byteBuffer.put((byte) 'd');
        ByteBufferUtil.debugAll(byteBuffer);
    }

    /**
     * ByteBuffer压缩数据
     */
    @Test
    public void testCompact() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入多个字节
        byteBuffer.put(new byte[]{'a','b','c'});

        //读取两个字节
        byteBuffer.flip();
        byteBuffer.get();
        byteBuffer.get();
        ByteBufferUtil.debugAll(byteBuffer);

        //压缩数据
        byteBuffer.compact();
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        //覆盖之前的数据写入新数据
        byteBuffer.put((byte) 'd');
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);
    }

    /**
     * 模拟网络请求的粘包：
     * 每条数据以\n作为结尾
     * 数据1：say hi!\nhello
     * 数据2： world~\nhi\n
     * 将数据拆分开并打印
     */
    @Test
    public void testStickingAndUnpacking() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //发送数据1
        //                     7     12
        byteBuffer.put("say hi!\nhello".getBytes());

        decodeData(byteBuffer);
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        //发送数据2
        byteBuffer.put(" world~\nhi\n".getBytes());
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓data2↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        decodeData(byteBuffer);
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓compact↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);
    }

    /**
     * 解析数据
     * @param byteBuffer
     */
    private void decodeData(ByteBuffer byteBuffer){
        //存储已读取的数据
        StringBuilder readBytes = new StringBuilder();
        //改为读模式，并获取limit
        int len = byteBuffer.flip().limit();
        for (int i = 0; i < len; i++) {
            byte currByte = byteBuffer.get(i);
            if(currByte == '\n'){
                //byte转string
                String data = new String(StringUtil.decodeHexDump(readBytes.toString()), StandardCharsets.UTF_8);
                System.out.println(data);
                //清空旧数据，准备接收新数据
                readBytes = new StringBuilder();
                //调整 position 保证 compact 运行后数据正确
                byteBuffer.position(i+1);
            } else {
                readBytes.append(StringUtil.byteToHexStringPadded(currByte));
            }
        }
        byteBuffer.compact();
    }


    /**
     * 切换读模式
     */
    @Test
    public void testFlip(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入多个字节
        byteBuffer.put("abcde".getBytes());
        ByteBufferUtil.debugAll(byteBuffer);
        //切换为读模式
        byteBuffer.flip();
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓flip↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        //读取全部数据
        int limit = byteBuffer.limit();
        for (int i = 0; i < limit; i++) {
            System.out.println(byteBuffer.get());
        }

        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);
    }

    /**
     * 读取值
     */
    @Test
    public void testGet(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入多个字节
        byteBuffer.put("abcde".getBytes());
        //切换为读模式
        byteBuffer.flip();
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓flip↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        //读取限制
        int limit = byteBuffer.limit();
        //通过索引读取，position值不变更
        for (int i = 0; i < limit; i=i+2) {
            System.out.println(byteBuffer.get(i));
        }
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get(index)↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        //使用position作为索引读取，position变更
        for (int i = 0; i < limit; i++) {
            System.out.println(byteBuffer.get());
        }
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);
    }

    /**
     * make和reset的使用
     */
    @Test
    public void testMakeAndReset(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入多个字节
        byteBuffer.put("abcde".getBytes());
        //切换为读模式
        byteBuffer.flip();

        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());
        byteBuffer.mark();
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()2↓↓↓make()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()4↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        byteBuffer.reset();
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓reset()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());

        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()5↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);
    }

    /**
     * reset() 的一些坑
     */
    @Test
    public void testResetException(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        //写入多个字节
        byteBuffer.put("abcde".getBytes());
        //切换为读模式
        byteBuffer.flip();

        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.get());
        byteBuffer.mark();
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓get()2↓↓↓make()↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        //position方法，当make值比 po
        byteBuffer.position(1);
        System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓position↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        ByteBufferUtil.debugAll(byteBuffer);

        System.out.println(byteBuffer.get());

        //发生异常，而且异常不可预知，只能try catch
        byteBuffer.reset();
    }

    @Test
    public void testString2ByteBuffer(){
        //字符串转ByteBuffer 方法1
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put("abcde".getBytes());
        //读模式
        byteBuffer.flip();

        //字符串转ByteBuffer  方法2：已经为读模式
        ByteBuffer byteBuffer1 = StandardCharsets.UTF_8.encode("12345");

        //字符串转ByteBuffer  方法3：已经为读模式
        ByteBuffer byteBuffer2 = ByteBuffer.wrap("abcde".getBytes());

        //ByteBuffer转字符串
        CharBuffer decode = StandardCharsets.UTF_8.decode(byteBuffer);
        System.out.println(decode);
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer1));
        System.out.println(StandardCharsets.UTF_8.decode(byteBuffer2));

    }




}
