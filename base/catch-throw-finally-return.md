# catch中抛出异常的同时finally中执行return,为什么抛出的这个异常无法捕获?

## 代码

```
public int m1() throws Exception {
	int i;
	try{
		i = 1/0;
    }catch (Exception e){
		throw new Exception(e);
    }finally {
		return i;
    }

}

public void m2() {

	try{
		m1();
    }catch (Exception e){
		//这里不执行
		e.printStackTrace();
    }

}

```