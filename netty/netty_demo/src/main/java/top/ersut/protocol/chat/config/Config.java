package top.ersut.protocol.chat.config;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class Config {

    static final Properties PROPERTIES = new Properties();
    static {
        try(InputStream inputStream = Config.class.getResourceAsStream("/application.properties")) {
            if (inputStream == null){
                log.warn("没有获取到配置文件");
            }else {
                PROPERTIES.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //获取端口
    public static int getServerPort() {
        String portStr = PROPERTIES.getProperty("chat.server.port", "18880");
        int port = Integer.parseInt(portStr);
        return port;
    }

    //获取序列化方式
    public static String getSerializar(){
        String serializar = PROPERTIES.getProperty("chat.serializar","Json");
        return serializar;
    }
}
