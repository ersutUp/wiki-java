package top.ersut;

import org.junit.Test;

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



}
