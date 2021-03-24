# 【源码解析】DeferredImportSelector 使用以及声明周期

> spring 版本：5.3.4

----------

## 作用

设置`@Import.value`为`DeferredImportSelector`的实现类，从而导入一组bean

## 使用

### 先看源码

```java
public interface DeferredImportSelector extends ImportSelector {

	/**
	 * Return a specific import group.
	 * <p>The default implementations return {@code null} for no grouping required.
	 * @return the import group class, or {@code null} if none
	 * @since 5.0
	 */
	@Nullable
	default Class<? extends Group> getImportGroup() {
		return null;
	}


	/**
	 * Interface used to group results from different import selectors.
	 * @since 5.0
	 */
	interface Group {

		/**
		 * Process the {@link AnnotationMetadata} of the importing @{@link Configuration}
		 * class using the specified {@link DeferredImportSelector}.
		 */
		void process(AnnotationMetadata metadata, DeferredImportSelector selector);

		/**
		 * Return the {@link Entry entries} of which class(es) should be imported
		 * for this group.
		 */
		Iterable<Entry> selectImports();


		/**
		 * An entry that holds the {@link AnnotationMetadata} of the importing
		 * {@link Configuration} class and the class name to import.
		 */
		class Entry {

			private final AnnotationMetadata metadata;

			private final String importClassName;

			public Entry(AnnotationMetadata metadata, String importClassName) {
				this.metadata = metadata;
				this.importClassName = importClassName;
			}

			...

		}
	}

}
```

- 可以看出 `DeferredImportSelector` 继承了 `ImportSelector`，也就是说`DeferredImportSelector`的功能也是使用`@Import`进行导入bean，不过 `DeferredImportSelector` 是导入**一组**bean(**注意是组**)
- 具体使用：
	- 实现`Group`接口,这个接口主要的作用是生成一个`Iterable<Entry>`
	- 实现`DeferredImportSelector`接口,该接口只有一个方法`getImportGroup`，这个方法的作用是指定具体使用哪个`Group`实现类
- `Entry`类的作用：存储注解元信息以及bean对应类的全路径

## 生命周期

1. 实例化 `DeferredImportSelector` 的实现类
2. 通过 `DeferredImportSelector#getImportGroup` 方法获取具体`Group`实现类
3. 实例化`Group`的实现类
4. 调用`Group#process`方法
5. 调用`Group#selectImports`方法获取`Iterable<Entry>`
6. 遍历`Iterable<Entry>`注册bean

### 源码分析

org.springframework.context.annotation.ConfigurationClassParser#parse:

```java
//DeferredImportSelector处理器
private final DeferredImportSelectorHandler deferredImportSelectorHandler = new DeferredImportSelectorHandler();
public void parse(Set<BeanDefinitionHolder> configCandidates) {
	for (BeanDefinitionHolder holder : configCandidates) {
		BeanDefinition bd = holder.getBeanDefinition();
		//DeferredImportSelector处理器 添加数据
		parse(bd.getBeanClassName(), holder.getBeanName());

	}
	//DeferredImportSelector处理器 开始处理
	this.deferredImportSelectorHandler.process();
}
```

- `parse(bd.getBeanClassName(), holder.getBeanName());`部分：
	
	1. org.springframework.context.annotation.ConfigurationClassParser#processConfigurationClass：
	
	```java
	protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
	
		...
	
		// Recursively process the configuration class and its superclass hierarchy.
		SourceClass sourceClass = asSourceClass(configClass, filter);
		do {
			sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
		}
	
		...
	}
	```
	
	2. org.springframework.context.annotation.ConfigurationClassParser#doProcessConfigurationClass
	
	```java
	protected final SourceClass doProcessConfigurationClass(
			ConfigurationClass configClass, SourceClass sourceClass, Predicate<String> filter)
			throws IOException {
	
		...
	
		// Process any @Import annotations
		processImports(configClass, sourceClass, getImports(sourceClass), filter, true);
	
		...
	
	}
	
	```
	
	3. org.springframework.context.annotation.ConfigurationClassParser#processImports：生命周期第一步：实例化 DeferredImportSelector 的实现类
	
	```java
	private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
			Collection<SourceClass> importCandidates, Predicate<String> exclusionFilter,
			boolean checkForCircularImports) {
	
		...
	
		for (SourceClass candidate : importCandidates) {
			if (candidate.isAssignable(ImportSelector.class)) {
				// Candidate class is an ImportSelector -> delegate to it to determine imports
				Class<?> candidateClass = candidate.loadClass();
				//生命周期第一步：实例化 DeferredImportSelector 的实现类
				ImportSelector selector = ParserStrategyUtils.instantiateClass(candidateClass, ImportSelector.class,this.environment, this.resourceLoader, this.registry);
	
				if (selector instanceof DeferredImportSelector) {
					this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
				}
	
				...
	
			}
	
			...
	
		}
	
		...
	
	}
	```
	
	4. org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorHandler#handle: 创建 DeferredImportSelector 持有器
	
	```java
	//DeferredImportSelector 持有器
	private List<DeferredImportSelectorHolder> deferredImportSelectors = new ArrayList<>();
	public void handle(ConfigurationClass configClass, DeferredImportSelector importSelector) {
		DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(configClass, importSelector);
		if (this.deferredImportSelectors == null) {
			DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
			handler.register(holder);
			handler.processGroupImports();
		}
		else {
			this.deferredImportSelectors.add(holder);
		}
	}
	```

- `this.deferredImportSelectorHandler.process();`部分：
	
	org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorHandler#process：注册 DeferredImportSelector.Group 处理器(`DeferredImportSelectorGroupingHandler`)并处理`Group`对象
	```java
	//DeferredImportSelector 持有器
	private List<DeferredImportSelectorHolder> deferredImportSelectors = new ArrayList<>();
	public void process() {
		List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
		this.deferredImportSelectors = null;
		try {
			if (deferredImports != null) {
				DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
				deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
				//注册 DeferredImportSelector.Group 处理器,并生成对应的Group对象
				deferredImports.forEach(handler::register);
				//处理Group对象
				handler.processGroupImports();
			}
		}
		finally {
			this.deferredImportSelectors = new ArrayList<>();
		}
	}
	```

	- `handler::register` 部分	
		1. org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGroupingHandler#register:通过`DeferredImportSelector#getImportGroup`方法获取Group对象并进行实例化。（生命周期的第二步与第三部）

		```java
		public void register(DeferredImportSelectorHolder deferredImport) {
			//生命周期第二步：通过 DeferredImportSelector#getImportGroup 方法获取具体 Group 实现类
			Class<? extends Group> group = deferredImport.getImportSelector().getImportGroup();
			DeferredImportSelectorGrouping grouping = this.groupings.computeIfAbsent(
					(group != null ? group : deferredImport),
					//生命周期第三步：实例化Group实现类
					key -> new DeferredImportSelectorGrouping(createGroup(group)));
			grouping.add(deferredImport);
			this.configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(),
					deferredImport.getConfigurationClass());
		}
		```

	-  `handler.processGroupImports()`部分
		
		1.  org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGroupingHandler#processGroupImports：处理`Group`对象
			```java
			public void processGroupImports() {
				for (DeferredImportSelectorGrouping grouping : this.groupings.values()) {
					Predicate<String> exclusionFilter = grouping.getCandidateFilter();
					//通过Group对象获取 Iterable<Entry>
					grouping.getImports()
						//生命周期第六步 遍历Iterable<Entry>注册bean
						.forEach(entry -> {
						ConfigurationClass configurationClass = this.configurationClasses.get(entry.getMetadata());
						//注册bean
						processImports(configurationClass, asSourceClass(configurationClass, exclusionFilter),
								Collections.singleton(asSourceClass(entry.getImportClassName(), exclusionFilter)),
								exclusionFilter, false);
						
						...

					});
				}
			}
			```

			- `grouping.getImports()`部分：
				1. org.springframework.context.annotation.ConfigurationClassParser.DeferredImportSelectorGrouping#getImports：调用多个`Group#process`方法（多个`DeferredImportSelector`使用同一个`Group`），再调用`Group#selectImports`方法获取`Iterable<Entry>`（生命周期的第四部与第五步）
				```java
				public Iterable<Group.Entry> getImports() {
					for (DeferredImportSelectorHolder deferredImport : this.deferredImports) {
						//生命周期第四步 调用Group#process方法
						this.group.process(deferredImport.getConfigurationClass().getMetadata(),
								deferredImport.getImportSelector());
					}
					//生命周期第五步 调用Group#selectImports方法获取Iterable<Entry>
					return this.group.selectImports();
				}
				```

	


