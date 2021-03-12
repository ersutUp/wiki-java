# boot 之 hello world

需求：请求“/hello”，返回"hello,world!"

开发工具：idea

[示例项目](./spring-boot-demo/hello-world)

**注**：需要maven基础

----------

## 解决繁琐依赖
导入一个包即可解决web应用的依赖，包管理可以使用`maven`或者`gradle`,这里使用`maven`。

**由于国内限制，更改中央库为阿里库**，[点击查看settings.xml文件](./maven/settings.xml)

### 创建pom文件

繁琐的依赖有spring boot提供的 `spring-boot-starter-parent` pom文件来解决，那我们的pom文件的父文件就是这个。

pom示例：

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
	<!--重点指定父pom，解决繁琐的依赖-->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>top.ersut.boot</groupId>
    <artifactId>spring-boot-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>


    <dependencies>
    </dependencies>

</project>

```

导入web应用的依赖包：

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
## 创建启动类（项目的入口）

这个类的作用是启动spring boot。

代码：

```
//标识为一个启动类
@SpringBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

}
```

注意：记得添加 `@SpringBootApplication` 注解，这个注解标识这个类是一个启动类。

## “/hello” 请求的处理

代码：

```
@RestController
public class HelloWorldController {

    @RequestMapping("/hello")
    public String helloWorld() {
        return "hello,world!";
    }
}
```

`@RestController`：这是一个复合索引,由`@Controller`和`@ResponseBody`组成，`@Controller`标识是一个bean，`@ResponseBody`标识是这个类中所有响应数据之间放在body内。

`@RequestMapping("/hello")`:标识是个这个方法接受 “/hello” 请求。

## 测试

1. 启动 `HelloWorldApplication`
2. 浏览器访问 http://localhost:8080/hello ，显示 hello,world! 则正常。

## 部署

Spring Boot 支持两种启动方式，以内置容器启动和以外置容器启动，例如Spring MVC就是以外置容器启动。

Spring Boot 以内置容器启动时，打包一个**特殊的jar包**，使用命令`java -jar xxx.jar`即可启动，方便、快捷且简单。

### 怎么打包这个特殊的jar包

Spring Boot 提供了一个maven插件进行打包

在maven中添加插件：

```
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

maven中指定打什么包：

```
<packaging>jar</packaging>
```

执行`mvn package`进行打包，最终生成hello-world-0.0.1-SNAPSHOT.jar文件，执行`java -jar hello-world-0.0.1-SNAPSHOT.jar`运行，访问 http://localhost:8080/hello 进行测试。

### 为什么说是一个特殊的jar包

Spring Boot所生成的jar包内含有项目中所有的资源以及要使用第三方jar包，另外Spring Boot在这个jar包中添入了一个特制的启动器，这个启动器使jar包内的资源以及第三方jar包可以使用。

## 总结

- 项目中没有导入复杂的依赖，体现了Spring boot的快速搭建和自动配置
- 运行项目时没有再安装tomcat等容器，Spring boot 使用了嵌入式容器，部署方便、快捷、简单
- 项目中没有任何xml配置文件，告别了spring的xml配置，这个功劳要归结于spring的配置类功能