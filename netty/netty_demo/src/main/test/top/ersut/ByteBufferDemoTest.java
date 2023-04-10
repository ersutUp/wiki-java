package top.ersut;

import org.junit.Test;
import top.ersut.utils.ByteBufferUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
        //2、往ByteBuffer中写入4个字节
        byteBuffer.put("abcd".getBytes());
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




}
