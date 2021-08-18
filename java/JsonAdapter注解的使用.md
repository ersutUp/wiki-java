# JsonAdapter注解的使用

## JsonAdapter的作用

由`TypeAdapter`实现其序列化以及反序列方法，通过`@JsonAdapter`配置`TypeAdapter`用于指定属性的序列化以及反序列化方式

## 自定义TypeAdapter

继承抽象类`TypeAdapter`，并重写其序列化方法（write）以及反序列化方法（read），以实现时间戳与LocalDateTime的转换，[部分代码示例](./java-demo/gosn-typeAdapter/src/main/java/top/ersut/java/gson/TimestampTypeAdapter.java):

```java
public class TimestampTypeAdapter<T> extends TypeAdapter<T> {
    final Class<T> clazz;

    private TimestampTypeAdapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 反序列化
     */
    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String json = in.nextString();
        //验证
        if (!Pattern.matches("\\d{10}|\\d{13}", json)) {
            throw new JsonSyntaxException(json, new Exception("not timestamp"));
        }

        int len = json.length();
        //10位时间戳进行修正
        if (len == 10) {
            json += "000";
        }

        ZonedDateTime zonedDateTime = Instant.ofEpochMilli(Long.valueOf(json)).atZone(ZoneId.systemDefault());
        if(clazz.isAssignableFrom(LocalDateTime.class)){
            return (T)zonedDateTime.toLocalDateTime();
        } else if(clazz.isAssignableFrom(Date.class)){
            return (T)Date.from(zonedDateTime.toInstant());
        }

        return null;
    }

    /**
     * 序列化
     */
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        if(clazz.isAssignableFrom(LocalDateTime.class)){
            LocalDateTime localDateTime = (LocalDateTime) value;
            out.value(localDateTime.toEpochSecond(OffsetDateTime.now().getOffset())*1000);
        } else if(clazz.isAssignableFrom(Date.class)){
            Date date = (Date) value;
            out.value(date.getTime());
        }
    }
    
    public final static class LocalDateTimeTypeAdapter extends TimestampTypeAdapter<LocalDateTime>{

        public LocalDateTimeTypeAdapter() {
            super(LocalDateTime.class);
        }

    }
}
```

## JsonAdapter使用

[代码示例：](./java-demo/gosn-typeAdapter/src/main/java/top/ersut/java/gson/pojo/Student.java)

```java
@JsonAdapter(TimestampTypeAdapter.LocalDateTimeTypeAdapter.class)
private LocalDateTime updateAt;
```

其实现了时间戳与LocalDateTime的序列化与反序列化

[测试用例](./java-demo/gosn-typeAdapter/src/test/java/top/ersut/java/gson/TimestampTypeAdapterTest.java)：

```java
@Test
public void testTypeAdapter(){
    String json = new Gson().toJson(Student.builder().name("学生").createAt(new Date()).updateAt(LocalDateTime.now()).build());
    System.out.println(json);
}
```

## 是否可以不指定@JsonAdapter来针对类型进行转换

答案是可以的通过注册`TypeAdapterFactory`即可，但是如果指定了`@JsonAdapter`则优先与注册的`TypeAdapterFactory`

[测试用例](./java-demo/gosn-typeAdapter/src/test/java/top/ersut/java/gson/TimestampTypeAdapterTest.java)：

```java
@Test
public void testTypeAdapterFactory(){
    String json = new GsonBuilder()
        .registerTypeAdapterFactory(TimestampTypeAdapter.DATE_FACTORY)
        .registerTypeAdapterFactory(TimestampTypeAdapter.LOCALDATETIME_FACTORY)
        .create()
        .toJson(Teacher.builder().name("老师").createAt(new Date()).updateAt(LocalDateTime.now()).build());
    System.out.println(json);
}
```

