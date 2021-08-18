package top.ersut.spring.config;

import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "top.ersut.spring")
public class ProjectConf {

    @Bean
    public JavaMailSenderImpl javaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("651100302@qq.com");
        javaMailSender.setPassword("fghfgh");
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MimeMessage mimeMessage(JavaMailSender javaMailSender) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setFrom(new InternetAddress("651100302@qq.com","消息提醒", "utf-8"));
        return mimeMessage;
    }

}
