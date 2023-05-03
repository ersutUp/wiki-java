package top.ersut;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileChannelTest {

    @Test
    public void getFileChannelTest(){
        String writeStr = "abcdef";
        //FileOutputStream 只能写入文件
        //FileOutputStream 自动创建文件，但是不创建目录
        //FileOutputStream 不需要flush，因为它是直接操作文件的
        try (FileOutputStream fileOutputStream = new FileOutputStream("fileChannel.txt")){
            //获取通道
            FileChannel fileChannel = fileOutputStream.getChannel();

            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(writeStr);

            //FileChannel.write 无法保证一次性写入所有字节，所以要循环引用直到 ByteBuffer 中没有了字节
            while (byteBuffer.hasRemaining()){
                fileChannel.write(byteBuffer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //FileInputStream 只能读取文件
        //FileInputStream 自动创建文件，但是不创建目录
        try (FileInputStream fileInputStream = new FileInputStream("fileChannel.txt")){
            //获取通道
            FileChannel fileChannel = fileInputStream.getChannel();

            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            //获取文件内容
            fileChannel.read(byteBuffer);
            byteBuffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);

            Assert.assertEquals(writeStr,charBuffer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void randomAccessFileTest() {
        String writeStr = "eeeeee";
        //RandomAccessFile自动创建文件，但是不创建目录
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("randomAccessFile.txt","rw")){
            FileChannel fileChannel = randomAccessFile.getChannel();

            //写入数据
            ByteBuffer writeByteBuffer = StandardCharsets.UTF_8.encode(writeStr);
            while (writeByteBuffer.hasRemaining()){
                fileChannel.write(writeByteBuffer);
            }

            //从头读取数据
            //指针放到头
            fileChannel.position(0);
            ByteBuffer readByteBuffer = ByteBuffer.allocate(10);
            //读取数据
            fileChannel.read(readByteBuffer);
            readByteBuffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(readByteBuffer);
            System.out.println(charBuffer);
            Assert.assertEquals(writeStr,charBuffer.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //文件的拷贝
    @Test
    public void fileChannelCopyTest(){
        getFileChannelTest();
        try(
                FileInputStream from = new FileInputStream("fileChannel.txt");
                FileOutputStream to = new FileOutputStream("fileChannel_copy.txt");
        ){
            FileChannel fromChannel = from.getChannel();
            FileChannel toChannel = to.getChannel();
            //单次拷贝只有2GB，超出2GB部分不会进行拷贝
            fromChannel.transferTo(0,fromChannel.size(),toChannel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //文件的拷贝
    @Test
    public void filesCopyTest(){
        getFileChannelTest();

        Path from = Paths.get("fileChannel.txt");
        Path to = Paths.get("fileChannel_copy1.txt");
        try {
            if(Files.notExists(to)){
                //该方式拷贝不限制文件的大小，目标文件存在会报错
                Files.copy(from,to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(Files.exists(to));

        try {
            //StandardCopyOption.REPLACE_EXISTING 代表如果目标文件存在进行覆盖操作
            Files.copy(from,to,StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(Files.exists(to));
    }

    //文件的移动
    @Test
    public void filesMoveTest() {
        this.filesCopyTest();

        Path source = Paths.get("fileChannel_copy1.txt");
        Path targer = Paths.get("fileChannel_copy2.txt");
        try {
            //StandardCopyOption.ATOMIC_MOVE 代表移动文件，并保证文件的原子性,同时可进行重命名
            Files.move(source,targer,StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(Files.exists(targer));
        Assert.assertTrue(Files.notExists(source));

    }

    //文件/目录 的删除
    @Test
    public void filesDeleteTest() {
        this.filesCopyTest();

        Path path = Paths.get("fileChannel_copy1.txt");
        try {
            //删除文件
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(Files.notExists(path));

        try {
            String deleteDirName = "deleteDir";
            Path dir = Paths.get(deleteDirName);
            //创建目录
            Files.createDirectory(dir);
            Assert.assertTrue(Files.exists(dir));

            //删除目录，注意当目录下有文件，删除目录会报异常
            Files.delete(dir);
            Assert.assertTrue(Files.notExists(dir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //创建目录树
    public void createDirTree(String dirName){
        Path dir = Paths.get(dirName);
        if(Files.exists(dir)){
            return;
        }
        try {
            //创建目录
            Files.createDirectory(dir);

            //创建文件
            Files.createFile(Paths.get(dirName,"1.txt"));
            Files.createFile(Paths.get(dirName,"2.txt"));
            Files.createFile(Paths.get(dirName,"3.txt"));

            //创建子目录
            String deleteChildrenDirName = dirName+"/children";
            Files.createDirectory(Paths.get(deleteChildrenDirName));
            //子目录下创建文件
            Files.createFile(Paths.get(deleteChildrenDirName,"c1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(Files.exists(dir));
    }

    //删除含有文件的目录
    @Test
    public void filesDeleteDirAndFileTest() {
        String deleteDirName = "deleteDir2";
        Path dir = Paths.get(deleteDirName);
        createDirTree(deleteDirName);

        //删除带文件的目录
        try {
            //遍历文件树
            //SimpleFileVisitor 文件树的实时回调
            Files.walkFileTree(dir,new SimpleFileVisitor<Path>(){

                //文件的回调
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return super.visitFile(file,attrs);
                }

                //对目录的后置回调
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return super.postVisitDirectory(dir,exc);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(Files.notExists(dir));
    }

    //打印文件树
    @Test
    public void fileTreePrintTest() {
        String deleteDirName = "deleteDir3";
        Path dir = Paths.get(deleteDirName);
        createDirTree(deleteDirName);

        final StringBuilder tab = new StringBuilder();
        try {
            //遍历文件数
            //SimpleFileVisitor 文件树的实时回调
            Files.walkFileTree(dir,new SimpleFileVisitor<Path>(){

                //目录的前置回调
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    tab.append(" ");
                    System.out.println(tab+dir.getFileName().toString());
                    return super.preVisitDirectory(dir, attrs);
                }

                //文件的回调
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println(tab+" |- "+file.getFileName());
                    return super.visitFile(file,attrs);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
