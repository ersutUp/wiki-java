# Spring Boot 2 中 yaml 的使用

> spring boot 版本：2.4.3
>
> [示例项目](./spring-boot-demo/anno-ConfigurationProperties-yaml)

## yaml 书写格式

### 代码示例

```yaml
key: value
  key2: value2
  key3: value3
#驼峰
user-name: wang
adminName: admin
```

### 注意点

- ":"后紧跟一个空格
- 严重依赖缩进，通过缩进来表示层级，比如上边示例中的 `key3`和`key2` 属于 `key`
- key的驼峰写法：
	- 第一种通过中划线：`user-name`，其对应的属性名为`userName`
	- 第二种直接按照驼峰来写：`adminName`，其对应的属性名为`adminName`
- key对于中文不友好会自动忽略
- 使用"#"作为注释符

## yaml 各种类型的写法

### 字面量

**示例：**

```yaml
age: 18
name: 张三
money: 19.9
is-student: true
```

字面量比较简单直接写就可以

[测试用例(literals方法)](./spring-boot-demo/anno-ConfigurationProperties-yaml/src/test/java/top/ersut/boot/yml/PeopleTest.java)

### 数组、List、Set

1. 行内方式（json），示例：

	```yaml
	books: [小狗钱钱,麦肯锡方法]
	```

2. 中划线（-）方式，示例

	```yaml
	books:
	  - 意志力
	  - Thinking in java
	```

数组有两种方式表示，**推荐使用方式一**，它与json的写法一致

[测试用例(array方法)](./spring-boot-demo/anno-ConfigurationProperties-yaml/src/test/java/top/ersut/boot/yml/PeopleTest.java)

### Map（键值对）

1. 行内方式（json），示例

	```yaml
	transport: {car: 1}
	```

2. 键值对方式，示例

	```yaml
	transport:
	  #key值对中文不友好，会自动忽略
	  中文: 5
	  bike: 1
	  car: 3
	```

Map有两种方式表示，**推荐使用方式一**，它与json的写法一致

[测试用例(keyValue方法)](./spring-boot-demo/anno-ConfigurationProperties-yaml/src/test/java/top/ersut/boot/yml/PeopleTest.java)

### 关于转义字符

直接写转义字符会被识别为字符串，需要加上双引号才能识别，示例：

```yaml
#双引号内的转义字符会正常执行
name: "张\n三"
#单引号内的转义字符按字符串处理
alias: '小\n三'
#与单引号处理方式一致
sex: 男\n人
```

单引号或者什么都不加都是按字符串处理。

[测试用例(escapeCharacter方法)](./spring-boot-demo/anno-ConfigurationProperties-yaml/src/test/java/top/ersut/boot/yml/PeopleTest.java)

## 延伸阅读

- [@ConfigurationProperties使用](./boot-annotation.md#ConfigurationProperties)
- @Value使用

