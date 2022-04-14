package xyz.ersut.security.securitydemo.config.filter.body.response;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * 响应体输出类
 *
 * @author 王二飞
 */
public final class ResponseBodyWrapper extends HttpServletResponseWrapper {

    //输出到byte array
    private ByteArrayOutputStream buffer = null;
    private ServletOutputStream out = null;
    private PrintWriter writer = null;
    private HttpServletResponse superResp = null;

    public ResponseBodyWrapper(HttpServletResponse resp) throws IOException {
        super(resp);
        superResp = resp;
        // 真正存储数据的流
        buffer = new ByteArrayOutputStream();
        out = new WapperedOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer));
    }

    public HttpServletResponse superResponse(){
        return superResp;
    }

    /**
     * 重载父类获取outputstream的方法
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    /**
     * 重载父类获取writer的方法
     */
    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        return writer;
    }

    /**
     * 重载父类获取flushBuffer的方法
     */
    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    /**
     * 将out、writer中的数据强制输出到WapperedResponse的buffer里面，否则取不到数据
     */
    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    /**
     * 内部类，对ServletOutputStream进行包装
     */
    private class WapperedOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos = null;

        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
            bos = stream;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            bos.write(b, 0, b.length);
        }


        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bos.write(b, off, len);
        }


        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}