# Java14 新特性

## 增加`record`类

```java
public record Point(int x, int y) {}
```

`Point`类翻译成class类如下：

```java
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }
  
    public String toString() {
        ...
    }

    public boolean equals(Object o) {
        ...
    }
    public int hashCode() {
        ...
    }
  
}
```

💡从这示例可以看出，**`record`类简化了`POJO`类**

### 构造方法

```java
public record Point(int x, int y) {
  public Point {
    if (x < 0 || y < 0) {
      throw new IllegalArgumentException("x and y must be non-negative");
    }
  }
}
```

💡通过构造方法可以对其属性进行校验等操作