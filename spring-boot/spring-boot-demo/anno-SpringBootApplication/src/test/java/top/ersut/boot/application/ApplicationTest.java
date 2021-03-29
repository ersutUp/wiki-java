package top.ersut.boot.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import top.ersut.boot.application.pojo.Admin;
import top.ersut.boot.scan.clazz.Stutdent;
import top.ersut.boot.scan.clazz.Teacher;
import top.ersut.boot.scan.path.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationTest {

    @Autowired
    private Application application;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void scanBasePackagesTest(){
        User user = applicationContext.getBean(User.class);
        Assertions.assertNotNull(user);
    }

    @Test
    void scanBasePackageClassesTest(){
        Stutdent stutdent = applicationContext.getBean(Stutdent.class);
        Assertions.assertNotNull(stutdent);
        Teacher teacher = applicationContext.getBean(Teacher.class);
        Assertions.assertNotNull(teacher);
    }

    @Test
    void excludeTest(){

        /*
        @Configuration(proxyBeanMethods = false)
        @EnableConfigurationProperties
        public class ConfigurationPropertiesAutoConfiguration {

        }


        这个会生效，但是由于我们排除了这个自动配置类所以不会生效
         */

        String[] applicationContextBeanNamesForType = applicationContext.getBeanNamesForType(ConfigurationPropertiesAutoConfiguration.class);
        Assertions.assertEquals(applicationContextBeanNamesForType.length,0);
    }
    @Test
    void excludeNameTest(){

        /*
        @ConditionalOnClass(ThreadPoolTaskScheduler.class)
        @Configuration(proxyBeanMethods = false)
        public class TaskSchedulingAutoConfiguration {
            ...
        }

        ThreadPoolTaskScheduler 在 spring-content 包 所以这个会生效，但是由于我们排除了这个自动配置类所以不会生效
         */

        String[] applicationContextBeanNamesForType = applicationContext.getBeanNamesForType(TaskSchedulingAutoConfiguration.class);
        Assertions.assertEquals(applicationContextBeanNamesForType.length,0);
    }

    @Test
    void proxyBeanMethodsTest(){
        Admin admin = applicationContext.getBean(Admin.class);
        Assertions.assertNotNull(admin);

        Admin admin1 = application.admin();
        Assertions.assertFalse(admin == admin1);

        Admin admin2 = application.admin();
        Assertions.assertFalse(admin == admin2);
        Assertions.assertFalse(admin1 == admin2);
    }

}