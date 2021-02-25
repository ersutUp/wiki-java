# AOP面向切面编程

## 什么是AOP
1. AOP（面向切面编程）是能够让我们在不影响原有功能的前提下，为软件**横向扩展功能,功能增强**。
2. AOP在不修改源代码的情况下添加新功能。
3. AOP降低了代码的耦合度，提高开发效率

## AOP的实现原理

AOP底层通过动态代理进行功能扩展

### 动态代理的两种方式
1. JDK动态代理
	1. 要代理的类必须是接口实现类
	2. 通过接口生成代理类，代理类进行功能扩展
	3. [项目示例](./spring-framework-demo/AOP-dynamicProxy-JDK),部分代码：
		1. 接口:必须有接口不然JDK动态代理无法实现
		```
		public interface UserServer {
		    void add();
		    void all();
		}
		```

		2. 调用处理器:动态代理处理的事情（invoke方法内,他是InvocationHandler的方法）
		```
		public class MyInvocationHandler implements InvocationHandler {

		    private Object obj;
		
		    public MyInvocationHandler(Object obj){
		        this.obj = obj;
		    }
		
		    @Override
		    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":before");
		        //调用被代理类的方法
		        Object result = method.invoke(obj,args);
		        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":after");
		        return result;
		    }
		}
		```

		3. 单元测试
			1. 创建代理对象方法：public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
				1. 形参 loader 是类加载器
				2. 形参 interfaces 是接口数组，填写被代理类实现的接口
				3. 形参 h 是调用处理器
				4. 返回对象是生成的代理类对象
			2. 代码
			```
		    @Test
		    void tset() {
		        //不使用代理
		        System.out.println("不使用代理\n");
		        UserServer userServer = new UserServerImpl();
		        userServer.add();
		        System.out.println();
		        userServer.all();
		
		        System.out.println("----------------------");
		
		        //使用代理
		        System.out.println("使用代理\n");
		        UserServer userServerProxy = (UserServer) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserServer.class},new MyInvocationHandler(new UserServerImpl()));
		        userServerProxy.add();
		        System.out.println();
		        userServerProxy.all();
		    }
			```
	4. 动态代理生成的代理类，[查看内存中的类](./../base/memory-class.md)
	```
	public final class $Proxy9 extends Proxy
	  implements UserServer
	{

	  ...

	  private static Method m3;
	
	  public $Proxy9(InvocationHandler paramInvocationHandler)
	  {
	    super(paramInvocationHandler);
	  }
	
	  public final void all()
	  {
	    try
	    {
		  //this.h 就是 MyInvocationHandler 实例
	      this.h.invoke(this, m3, null);
	      return;
	    }
	    catch (RuntimeException localRuntimeException)
	    {
	      throw localRuntimeException;
	    }
	    catch (Throwable localThrowable)
	    {
	    }
	    throw new UndeclaredThrowableException(localThrowable);
	  }
	
	  static
	  {
	    try
	    {

		  ...

	      m3 = Class.forName("top.ersut.spring.server.UserServer").getMethod("all", new Class[0]);

		  ...

	      return;
	    }
	    catch (NoSuchMethodException localNoSuchMethodException)
	    {
	      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
	    }
	    catch (ClassNotFoundException localClassNotFoundException)
	    {
	    }
	    throw new NoClassDefFoundError(localClassNotFoundException.getMessage());
	  }
	
	  ...

	}
	```

2. CGLIB动态代理
	1. 生成当前类的子类（代理类），进行扩展
	2. [项目示例](./spring-framework-demo/AOP-dynamicProxy-CGLIB),部分代码
		1. 被代理的类
		```
		public class UserServer {
		    public void add() {
		        System.out.println(this.getClass().getSimpleName()+".add:run");
		    }
		
		    public void all() {
		        System.out.println(this.getClass().getSimpleName()+".all:run");
		    }
		}
		```

		2. 回调类，类似JDK动态代理的调用处理器
		```
		public class MyMethodInterceptor implements MethodInterceptor {

		    /**
		     * @param obj 代理类
		     * @param method 被代理类的方法
		     * @param args 方法的参数
		     * @param proxy 代理类调用父类的方法（super.xxx()）
		     */
		    @Override
		    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":before");
		        //调用被代理类的方法,注意是invokeSuper
		        Object result = proxy.invokeSuper(obj,args);
		        System.out.println(obj.getClass().getSimpleName()+"."+method.getName()+":after");
		        return result;
		    }
		}
		```

		3. 测试方法
		```
	    @Test
	    void tset() {
	        //不使用代理
	        System.out.println("不使用代理\n");
	        UserServer userServer = new UserServerImpl();
	        userServer.add();
	        System.out.println();
	        userServer.all();
	
	        System.out.println("----------------------");
	
	        //使用代理
	        System.out.println("使用代理\n");
	        UserServer userServerProxy = (UserServer) Proxy.newProxyInstance(this.getClass().getClassLoader(),new Class[]{UserServer.class},new MyInvocationHandler(new UserServerImpl()));
	        userServerProxy.add();
	        System.out.println();
	        userServerProxy.all();
	    }
		```

		4. 生成的代理类,部分代码
		```
		public class UserServer$$EnhancerByCGLIB$$12fbbc62 extends UserServer implements Factory {

			...

		    private static final Method CGLIB$all$0$Method;
    		private static final MethodProxy CGLIB$all$0$Proxy;
			private static final Object[] CGLIB$emptyArgs;

			...

		    static {
		        CGLIB$STATICHOOK1();
		    }

		    static void CGLIB$STATICHOOK1() {

				...

				CGLIB$emptyArgs = new Object[0];

				//代理类
		        Class var0 = Class.forName("top.ersut.spring.server.UserServer$$EnhancerByCGLIB$$12fbbc62");
				//被代理类
		        Class var1 = Class.forName("top.ersut.spring.server.UserServer");

		        Method[] var10000 = ReflectUtils.findMethods(new String[]{"all", "()V", "add", "()V"}, var1.getDeclaredMethods());

				//被代理类的方法
		        CGLIB$all$0$Method = var10000[0];
				//代理类调用父类的方法（super.xxx()）
		        CGLIB$all$0$Proxy = MethodProxy.create(var1, var0, "()V", "all", "CGLIB$all$0");
				
				...

		    }
					
		    final void CGLIB$all$0() {
		        super.all();
		    }

		    public final void all() {
				//回调类
		        MethodInterceptor var10000 = this.CGLIB$CALLBACK_0;
		        if (var10000 == null) {
		            CGLIB$BIND_CALLBACKS(this);
		            var10000 = this.CGLIB$CALLBACK_0;
		        }
		
		        if (var10000 != null) {
		            var10000.intercept(this, CGLIB$all$0$Method, CGLIB$emptyArgs, CGLIB$all$0$Proxy);
		        } else {
		            super.all();
		        }
		    }

		}
		```

### AOP具体使用哪种动态代理
因为JDK动态代理仅支持实现接口的类，所以当具体切面的方法有对应接口那么使用JDK动态代理否则使用CGLIB动态代理。


