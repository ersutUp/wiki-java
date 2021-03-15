# Spring Boot 的注解

----------

## @SpringBootTest
- 功能：测试类添加SpringBoot环境
- 可用处：测试类
- 代码示例：
	```
	@SpringBootTest
	class StudentServiceTest {
	}
	```


## @ImportResource

- 功能：导入xml配置
- 可用处：一般放在配置类或启动类上
- 代码示例：
	```
	@SpringBootApplication
	@ImportResource("classpath:beans.xml")
	public class Application {
	
	    public static void main(String[] args) {
	        SpringApplication.run(Application.class,args);
	    }
	
	}
	```


## @Configuration

- 功能：替代之前spring中xml配置文件  
- 可用处：类
- 代码示例：
	```
	@Configuration
	public class ProjectConf {
	}
	```

## @Bean

- 功能：创建一个bean，等同于xml中的bean标签
- 可用处：一般在配置类的方法上
- 说明：方法名作为bean的名称（也可指定，未指定时使用方法名），返回值作为bean的实例
- 代码示例：
	```
	@Configuration
	public class ProjectConf {
	
	    //使用方法名作为bean的名称
	    @Bean
	    public StudentService studentService(){
	        return new StudentServiceImpl("studentService");
	    }
	
	    //指定bean的名称
	    @Bean("studentService01")
	    public StudentService studentServiceTow(){
	        return new StudentServiceImpl("studentService01");
	    }
	
	}
	```

**[@SpringBootTest、@ImportResource、@Configuration、@Bean 示例项目](./spring-boot-demo/anno-conf)**

