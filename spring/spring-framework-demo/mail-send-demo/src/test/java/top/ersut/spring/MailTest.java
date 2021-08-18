package top.ersut.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import top.ersut.spring.config.ProjectConf;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.Annotation;

public class MailTest {

    @Test
    public void sendTest() throws MessagingException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProjectConf.class);
        JavaMailSender javaMailSender = applicationContext.getBean(JavaMailSender.class);
        MimeMessage mimeMessage = applicationContext.getBean(MimeMessage.class);

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo("83889573@qq.com");
        mimeMessageHelper.setSubject("标题");
        mimeMessageHelper.setText("测试邮件"+System.nanoTime());
        javaMailSender.send(mimeMessage);
    }

}
