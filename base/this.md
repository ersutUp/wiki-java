# this 关键字

----------

## 继承

代码：

Pat类:

```
public class Pat {

    public void aaa(){
        System.out.println("aaa");
    }

    public void bbb(){
        this.aaa();
        System.out.println("bbb");
    }

}
```

Cat类(继承Pat类):

```
public class Cat extends Pat {


    @Override
    public void aaa(){
        System.out.println("Pat.aaa");
    }

    public static void main(String[] args) {
        new Cat().bbb();
    }

}
```

运行`Cat.main`方法,输出结果：

```
Pat.aaa
bbb
```

**堪称一道面试题啊！**