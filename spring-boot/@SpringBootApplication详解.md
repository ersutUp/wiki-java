# @SpringBootApplication 详解

> spring boot 版本：2.4.3

## 作用

**标识某个类为启动类,即程序的入口**

## 使用

- 添加在类上
- 这个注解为复合注解，由`@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan` 组成，后续会详细介绍
- 属性：
	- exclude：按照 class 排查一个或多个自动配置类，`@EnableAutoConfiguration.exclude` 的别名
	- excludeName：按照 类全路径 排除一个或多个自动配置类，`@EnableAutoConfiguration.excludeName` 的别名
	- scanBasePackages：按照 包路劲 添加一个或多个扫描包，`@ComponentScan.basePackages` 的别名
	- scanBasePackageClasses：按照 类所属包 添加一个或多个扫描包，`@ComponentScan.basePackageClasses` 的别名
	- nameGenerator：Bean名称生成规则，`@ComponentScan.nameGenerator` 的别名
	- proxyBeanMethods：当启动类中使用`@Bean`来注册bean时，如果该属性为`true`时不管调用多少次`@Bean`对应方法返回的都是容器中的bean；如果该属性为`false`时每次调用`@Bean`对应方法返回的不同（指地址不同，类型相同）的对象。该属性默认值为`true`，`@Configuration.proxyBeanMethods` 的别名， `@SpringBootConfiguration` 中包含 `@Configuration`
- 该注解默认扫描包为这个类的所属包，如果有其他包需要使用 `scanBasePackages`或`scanBasePackageClasses`指定
- [测试用例](./spring-boot-demo/anno-SpringBootApplication/src/test/java/top/ersut/boot/application/ApplicationTest.java)
- 示例代码：

```java
@SpringBootApplication(
		scanBasePackages = "top.ersut.boot.scan.path"
        ,scanBasePackageClasses = Stutdent.class
        ,exclude = ConfigurationPropertiesAutoConfiguration.class
        ,excludeName = "org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration"
        ,proxyBeanMethods = false)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 源码分析

### `@SpringBootApplication` 源码解析:

**源码：**


```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

	...

}
```
从源码中可看出 `@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan` 组成的 `@SpringBootApplication`，这是个复合注解。

### `@SpringBootConfiguration` 源码解析

**源码：**

```java
@Configuration
public @interface SpringBootConfiguration {
	@AliasFor(annotation = Configuration.class)
	boolean proxyBeanMethods() default true;
}
```

`@SpringBootConfiguration`只有一个属性`proxyBeanMethods`，而且这个属性还是`@Configuration.proxyBeanMethods`的别名，所以这个注解与`@Configuration`功能一致。

`@SpringBootConfiguration`只允许在应用程序中使用一次，通常由`@SpringBootApplication`继承而使用。

### `@ComponentScan` ：设置扫描包路径

在`@SpringBootApplication`中设置了两个排除过滤器(`TypeExcludeFilter`与`AutoConfigurationExcludeFilter`)`@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })` ，这两个过滤器过滤了什么？

#### 先说说过滤器怎么用

**@Filter源码：**

```java
@interface Filter {

    FilterType type() default FilterType.ANNOTATION;

    @AliasFor("classes")
    Class<?>[] value() default {};

    @AliasFor("value")
    Class<?>[] classes() default {};

    ...

}
```

`type`属性：设置过滤器的类型，当值为`FilterType.CUSTOM`时为自定义过滤器

`value`属性与`classes`属性相互别名所以作用一样

`classes`属性的作用：设置过滤器类，当设置自定义过滤类时，这个过滤类需要实现`TypeFilter`接口，[具体参考官方文档](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.Filter.html#classes--)

**`TypeFilter`源码：**

```java
public interface TypeFilter {

   boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
         throws IOException;

}
```

`TypeFilter`只有一个方法`#match`，当这个方法返回`true`时进行过滤，`false`不过滤。

综上所述我们查看自定义过滤器源码时，入口为`#match`方法

#### `TypeExcludeFilter` 源码解析

**源码：** 主要看`#match`和`#getDelegates`方法

```java
public class TypeExcludeFilter implements TypeFilter, BeanFactoryAware {

	private BeanFactory beanFactory;

	private Collection<TypeExcludeFilter> delegates;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		if (this.beanFactory instanceof ListableBeanFactory && getClass() == TypeExcludeFilter.class) {
			//遍历所有TypeExcludeFilter类型的bean
			for (TypeExcludeFilter delegate : getDelegates()) {
				//执行bean的match方法
				if (delegate.match(metadataReader, metadataReaderFactory)) {
					return true;
				}
			}
		}
		return false;
	}

	private Collection<TypeExcludeFilter> getDelegates() {
		Collection<TypeExcludeFilter> delegates = this.delegates;
		if (delegates == null) {
			//获取所有的TypeExcludeFilter类型的bean
			delegates = ((ListableBeanFactory) this.beanFactory).getBeansOfType(TypeExcludeFilter.class).values();
			this.delegates = delegates;
		}
		return delegates;
	}
    ...
}
```

**作用**：可以看出这个过滤器是一个扩展口。只要继承了`TypeExcludeFilter`并设置为bean（注解生成bean无法在 这个时期完成注册，[查看ApplicationContextInitializer实现类](./spring-boot-demo/anno-ComponentScan-TypeExcludeFilter/src/main/java/top/ersut/boot/filter/MyTypeExcludeFilterApplicationContextInitializer.java)）就会执行其`#match`方法。

**回到`@SpringBootConfiguration`中使用的`@ComponentScan`**

源码为：`@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class))`

功能：`TypeExcludeFilter`在`excludeFilters`属性下 ，那么`TypeExcludeFilter`的继承类中所匹配的数据都将过滤掉

**[示例](./spring-boot-demo/anno-ComponentScan-TypeExcludeFilter/)：**

[TypeExcludeFilter实现示例](./spring-boot-demo/anno-ComponentScan-TypeExcludeFilter/src/main/java/top/ersut/boot/filter/MyTypeExcludeFilter.java)，代码：

```java
public class MyTypeExcludeFilter extends TypeExcludeFilter {

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory){

        if ( metadataReader.getClassMetadata().getClassName().equals("top.ersut.boot.filter.pojo.Student") ) {
            return true;
        }

        return false;

    }

}
```

[对应测试用例](./spring-boot-demo/anno-ComponentScan-TypeExcludeFilter/src/test/java/top/ersut/boot/filter/TypeExcludeFilterImplTest.java),代码：

```java
@Test
void studentBeanTest() {
    boolean flag = applicationContext.containsBean("student");
    Assertions.assertFalse(flag);

    Room room = applicationContext.getBean(Room.class);
    Assertions.assertNotNull(room);
}
```

注意：[spring.factories文件](./spring-boot-demo/anno-ComponentScan-TypeExcludeFilter/src/main/resources/META-INF/spring.factories)

#### `AutoConfigurationExcludeFilter` 源码解析

**源码：**

```java
public class AutoConfigurationExcludeFilter implements TypeFilter, BeanClassLoaderAware {

   private ClassLoader beanClassLoader;

   private volatile List<String> autoConfigurations;

   @Override
   public void setBeanClassLoader(ClassLoader beanClassLoader) {
      this.beanClassLoader = beanClassLoader;
   }

   @Override
   public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
         throws IOException {
      //匹配含有 Configuration 注解的自动配置类
      return isConfiguration(metadataReader) && isAutoConfiguration(metadataReader);
   }

   //是否含有Configuration注解
   private boolean isConfiguration(MetadataReader metadataReader) {
      return metadataReader.getAnnotationMetadata().isAnnotated(Configuration.class.getName());
   }

   //当前类是否为自动配置类
   private boolean isAutoConfiguration(MetadataReader metadataReader) {
      return getAutoConfigurations().contains(metadataReader.getClassMetadata().getClassName());
   }

   //获取所有 org.springframework.boot.autoconfigure.EnableAutoConfiguration（自动配置类） 扩展项
   protected List<String> getAutoConfigurations() {
      if (this.autoConfigurations == null) {
         this.autoConfigurations = SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,
               this.beanClassLoader);
      }
      return this.autoConfigurations;
   }

}
```

作用：配含有`Configuration`注解的自动配置类

**回到`@SpringBootConfiguration`中使用的`@ComponentScan`**

源码为：`@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class))`

功能：`TypeExcludeFilter`在`excludeFilters`属性下 ，那么扫描包内含有`Configuration`注解的自动配置类都过滤掉

### `@EnableAutoConfiguration` 源码解析

**`@EnableAutoConfiguration`源码：**

```java
package org.springframework.boot.autoconfigure;

@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
   String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
   //按类排除自动配置类
   Class<?>[] exclude() default {};
   //按名称排除自动配置类
   String[] excludeName() default {};
}
```

可以看出是个复合注解由`@AutoConfigurationPackage`和`@Import(AutoConfigurationImportSelector.class)`组成。

#### `@AutoConfigurationPackage`的作用

**`@AutoConfigurationPackage`源码：**

```java
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {
	...
}
```

`@AutoConfigurationPackage`通过`@Import(AutoConfigurationPackages.Registrar.class)`导入了bean，那看看导入了写什么bean。

**`AutoConfigurationPackages.Registrar`源码：**

```java
package org.springframework.boot.autoconfigure;

public abstract class AutoConfigurationPackages {

   private static final Log logger = LogFactory.getLog(AutoConfigurationPackages.class);
   //org.springframework.boot.autoconfigure.AutoConfigurationPackages
   private static final String BEAN = AutoConfigurationPackages.class.getName();

   public static void register(BeanDefinitionRegistry registry, String... packageNames) {

         ...
         
         //注册bean定义
         registry.registerBeanDefinition(BEAN, new BasePackagesBeanDefinition(packageNames));
      }
   }

   /**
    * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
    * configuration.
    */
   static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

      @Override
      public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
         //注册 自动配置扫描包 的baen
         register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
      }

      @Override
      public Set<Object> determineImports(AnnotationMetadata metadata) {
         return Collections.singleton(new PackageImports(metadata));
      }

   }

	/**
	 * Wrapper for a package import.
	 */
	private static final class PackageImports {

		private final List<String> packageNames;

		PackageImports(AnnotationMetadata metadata) {
             //获取注解属性确定最终的 扫描包
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(AutoConfigurationPackage.class.getName(), false));
			List<String> packageNames = new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));
			for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
				packageNames.add(basePackageClass.getPackage().getName());
			}
			if (packageNames.isEmpty()) {
				packageNames.add(ClassUtils.getPackageName(metadata.getClassName()));
			}
			this.packageNames = Collections.unmodifiableList(packageNames);
		}
		
        ...

	}

   /**
    * Holder for the base package (name may be null to indicate no scanning).
    */
   static final class BasePackages {

      private final List<String> packages;

      private boolean loggedBasePackageInfo;

      BasePackages(String... names) {
         List<String> packages = new ArrayList<>();
         for (String name : names) {
            if (StringUtils.hasText(name)) {
               packages.add(name);
            }
         }
         this.packages = packages;
      }

      List<String> get() {
         if (!this.loggedBasePackageInfo) {
            if (this.packages.isEmpty()) {
               if (logger.isWarnEnabled()) {
                  logger.warn("@EnableAutoConfiguration was declared on a class "
                        + "in the default package. Automatic @Repository and "
                        + "@Entity scanning is not enabled.");
               }
            }
            else {
               if (logger.isDebugEnabled()) {
                  String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
                  logger.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames
                        + "'. Automatic @Repository and @Entity scanning is enabled.");
               }
            }
            this.loggedBasePackageInfo = true;
         }
         return this.packages;
      }

   }

   static final class BasePackagesBeanDefinition extends GenericBeanDefinition {

      private final Set<String> basePackages = new LinkedHashSet<>();

      BasePackagesBeanDefinition(String... basePackages) {
         setBeanClass(BasePackages.class);
         setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
         addBasePackages(basePackages);
      }

      @Override
      public Supplier<?> getInstanceSupplier() {
         return () -> new BasePackages(StringUtils.toStringArray(this.basePackages));
      }

      private void addBasePackages(String[] additionalBasePackages) {
         this.basePackages.addAll(Arrays.asList(additionalBasePackages));
      }

   }

}
```

- `AutoConfigurationPackages.Registrar`类实现了`ImportBeanDefinitionRegistrar`接口，所以我们先查看`AutoConfigurationPackages.Registrar#registerBeanDefinitions`方法
- `AutoConfigurationPackages.Registrar#registerBeanDefinitions`方法 new 了一个 `PackageImports`类，这个类用来解析`@AutoConfigurationPackage`注解，获取该注解所在类的包路径以及注解的属性值确定最终的扫描包
- `AutoConfigurationPackages.Registrar#registerBeanDefinitions`方法最终调用了`AutoConfigurationPackages#register`方法来注册bean定义

**那么`@AutoConfigurationPackage`注解最终实现了自动配置扫描包，默认情况下这个包是启动类所在的包路径**

#### `@Import(AutoConfigurationImportSelector.class)`的作用

**`AutoConfigurationImportSelector`源码解析：**

1. `AutoConfigurationImportSelector` 实现了 `DeferredImportSelector`接口

	```java
	// 实现了 DeferredImportSelector 接口
	public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
			ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
		...
	}
	```

	

2. `DeferredImportSelector`接口的入口方法为 `#getImportGroup`（具体原因请看[DeferredImportSelector源码解析 ](../spring/deferredImportSelector-souce.md)），其实现为:

	```java
	public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
			ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
		...
		@Override
		public Class<? extends Group> getImportGroup() {
			return AutoConfigurationGroup.class;
		}
		...
	}
	```

3. 根据其实现来查看`AutoConfigurationGroup`源码

	1. `AutoConfigurationGroup#process`方法：获取所有自动配置

		```java
		private static class AutoConfigurationGroup
					implements DeferredImportSelector.Group, BeanClassLoaderAware, BeanFactoryAware, ResourceLoaderAware {
		
		    ...
		    
		    @Override
		    public void process(AnnotationMetadata annotationMetadata, DeferredImportSelector deferredImportSelector) {
		        Assert.state(deferredImportSelector instanceof AutoConfigurationImportSelector,
		                     () -> String.format("Only %s implementations are supported, got %s",
		                                         AutoConfigurationImportSelector.class.getSimpleName(),
		                                         deferredImportSelector.getClass().getName()));
		        //获取所有自动配置类
		        AutoConfigurationEntry autoConfigurationEntry = ((AutoConfigurationImportSelector) deferredImportSelector)
		            .getAutoConfigurationEntry(annotationMetadata);
		        this.autoConfigurationEntries.add(autoConfigurationEntry);
		        for (String importClassName : autoConfigurationEntry.getConfigurations()) {
		            this.entries.putIfAbsent(importClassName, annotationMetadata);
		        }
		    }
		    
		    ...
		
		}
		```

		1. `#getAutoConfigurationEntry`方法：在`META-INF/spring.factories`文件中获取所有自动配置类

			```java
			public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
					ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
				...
				protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
					if (!isEnabled(annotationMetadata)) {
						return EMPTY_ENTRY;
					}
					AnnotationAttributes attributes = getAttributes(annotationMetadata);
					//在META-INF/spring.factories文件中获取自动配置类
					List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
					configurations = removeDuplicates(configurations);
					Set<String> exclusions = getExclusions(annotationMetadata, attributes);
					checkExcludedClasses(configurations, exclusions);
					configurations.removeAll(exclusions);
					configurations = getConfigurationClassFilter().filter(configurations);
					fireAutoConfigurationImportEvents(configurations, exclusions);
					return new AutoConfigurationEntry(configurations, exclusions);
				}
				...
			}
			```

			`META-INF/spring.factories`文件示例：

			```properties
			# Auto Configure
			org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
			org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
			org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
			org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
			org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
			org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
			org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
			org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
			org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration,\
			org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
			```

		2. `#getCandidateConfigurations`源码：

			```java
			package org.springframework.boot.autoconfigure;
			import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
			
			public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware,
					ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
				...
				protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
				    //在META-INF/spring.factories文件中获取自动配置类
			        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(),
							getBeanClassLoader());
					Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you "
							+ "are using a custom packaging, make sure that file is correct.");
					return configurations;
				}
				protected Class<?> getSpringFactoriesLoaderFactoryClass() {
					return EnableAutoConfiguration.class;
				}
				...
			}
			```

		3. `SpringFactoriesLoader#loadFactoryNames`源码：

			```java
			public final class SpringFactoriesLoader {
				...
				public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
				...
				public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
					ClassLoader classLoaderToUse = classLoader;
					if (classLoaderToUse == null) {
						classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
					}
					String factoryTypeName = factoryType.getName();
					return loadSpringFactories(classLoaderToUse).getOrDefault(factoryTypeName, Collections.emptyList());
				}
			    //所有spring.factories文件中的所有配置
				private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) {
					Map<String, List<String>> result = cache.get(classLoader);
					if (result != null) {
						return result;
					}
			
					result = new HashMap<>();
					try {
						Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
						while (urls.hasMoreElements()) {
							URL url = urls.nextElement();
							UrlResource resource = new UrlResource(url);
							Properties properties = PropertiesLoaderUtils.loadProperties(resource);
							for (Map.Entry<?, ?> entry : properties.entrySet()) {
								String factoryTypeName = ((String) entry.getKey()).trim();
								String[] factoryImplementationNames =
										StringUtils.commaDelimitedListToStringArray((String) entry.getValue());
								for (String factoryImplementationName : factoryImplementationNames) {
									result.computeIfAbsent(factoryTypeName, key -> new ArrayList<>())
											.add(factoryImplementationName.trim());
								}
							}
						}
						// Replace all lists with unmodifiable lists containing unique elements
						result.replaceAll((factoryType, implementations) -> implementations.stream().distinct()
								.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
						cache.put(classLoader, result);
					} catch (IOException ex) {
						throw new IllegalArgumentException("Unable to load factories from location [" +
								FACTORIES_RESOURCE_LOCATION + "]", ex);
					}
					return result;
				}
			    ...
			}
			```

	2. `AutoConfigurationGroup#process`方法：获取所有自动配置

		```java
		private static class AutoConfigurationGroup
					implements DeferredImportSelector.Group, BeanClassLoaderAware, BeanFactoryAware, ResourceLoaderAware {
			...
			@Override
		    public Iterable<Entry> selectImports() {
		        if (this.autoConfigurationEntries.isEmpty()) {
		            return Collections.emptyList();
		        }
		        //获取所有排除的自动配置类
		        Set<String> allExclusions = this.autoConfigurationEntries.stream()
		            .map(AutoConfigurationEntry::getExclusions).flatMap(Collection::stream).collect(Collectors.toSet());
		        Set<String> processedConfigurations = this.autoConfigurationEntries.stream()
		            .map(AutoConfigurationEntry::getConfigurations).flatMap(Collection::stream)
		            .collect(Collectors.toCollection(LinkedHashSet::new));
		        processedConfigurations.removeAll(allExclusions);
		
		        return sortAutoConfigurations(processedConfigurations, getAutoConfigurationMetadata()).stream()
		            .map((importClassName) -> new Entry(this.entries.get(importClassName), importClassName))
		            .collect(Collectors.toList());
		    }
		    ...
		}
		```

**`@Import(AutoConfigurationImportSelector.class)`最终导入了排除后的自动配置类**

#### **考虑一个问题：`spring-boot-autoconfigure-2.4.3.jar`那么多自动配置类我们项目里全都用的到吗？**

看看`spring-boot-autoconfigure-2.4.3.jar`中的几个自动配置类

- redis的自动配置类`RedisAutoConfiguration.java`源码：

	```java
	package org.springframework.boot.autoconfigure.data.redis;
	
	...
	import org.springframework.data.redis.core.RedisOperations;
	...
	
	@Configuration(proxyBeanMethods = false)
	//依赖RedisOperations类
	@ConditionalOnClass(RedisOperations.class)
	...
	public class RedisAutoConfiguration {
	    ...
	}
	```

	`RedisAutoConfiguration.java`中有个条件注解`@ConditionalOnClass(RedisOperations.class)`，注解含义：项目中有`RedisOperations`类才执行这个自动配置类。

- HttpEncoding的自动配置类`HttpEncodingAutoConfiguration.java`源码：

	```java
	package org.springframework.boot.autoconfigure.web.servlet;
	
	...
	import org.springframework.web.filter.CharacterEncodingFilter;
	
	@Configuration(proxyBeanMethods = false)
	...
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	@ConditionalOnClass(CharacterEncodingFilter.class)
	@ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)
	public class HttpEncodingAutoConfiguration {
	
	    ...
	
	}
	
	```

	`HttpEncodingAutoConfiguration.java`有三个条件注解：

	- `@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)`：必须是servlet容器的web环境
	- `@ConditionalOnClass(CharacterEncodingFilter.class)`：必须有`CharacterEncodingFilter`类
	- `@ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)`：`server.servlet.encoding`配置的值必须是`enabled`或者没有这个配置项

可以看出自动配置类是**根据条件注解按需加载的**，并不是所有都会执行。



