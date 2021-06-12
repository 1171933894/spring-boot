/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.jdbc;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.sql.XADataSource;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link DataSource}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Kazuki Shimizu
 * @since 1.0.0
 */

/**
 * DataSourceAutoConfiguration 功能概况
 *
 * 1）初始化 DataSourceProperties 配置文件
 * 2）初始化数据源
 * 3）执行 sql 文件
 * 4）为数据源注册一个 DataSourcePoolMetadataProvider 实例
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
// 从配置文件中映射 DataSource 的值
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ DataSourcePoolMetadataProvidersConfiguration.class, DataSourceInitializationConfiguration.class })
public class DataSourceAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	// 判断是否引入 内置数据库：H2，DERBY，HSQL
	@Conditional(EmbeddedDatabaseCondition.class)
	// 如果这是没有DataSource/XADataSource 对应的 BeanDefinition，就通过导入 EmbeddedDataSourceConfiguration.class 来，配置内置数据库对应的数据源！
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import(EmbeddedDataSourceConfiguration.class)
	protected static class EmbeddedDatabaseConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	// 判断是否引入依赖的数据源：HikariDataSource、tomcat.jdbc.pool.DataSource、BasicDataSource
	@Conditional(PooledDataSourceCondition.class)
	// 如果这是没有DataSource/XADataSource 对应的 BeanDefinition，就通过以下属性的配置文件，配置数据源！
	// 配置数据源的时候，如果没有指定一些数据库的参数，就会报错哦
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import({ DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class,
			DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.Generic.class,
			DataSourceJmxConfiguration.class })
	//满足其中的任意一个：1）有spring.datasource.type属性 2）满足PooledDataSourceAvailableCondition：项目中引入了数据源依赖
	protected static class PooledDataSourceConfiguration {

	}

	/**
	 * {@link AnyNestedCondition} that checks that either {@code spring.datasource.type}
	 * is set or {@link PooledDataSourceAvailableCondition} applies.
	 */
	//继承了 AnyNestedCondition 的类，会对这个类中的所有内部类(不一定非得是静态内部类)上的注解做匹配，只要其中有一个匹配了，就匹配了
	//说明：如果没有spring.datasource.type属性，就默认查看项目中有没有引入：hikari，tomcat，dbcp2。这样说明如果项目中exclude了这3个，那么就必须使用 spring.datasource.type来指定数据库连接池了
	//type 属性优先级比较低，是在找不到，就通过 DataSour
	static class PooledDataSourceCondition extends AnyNestedCondition {

		PooledDataSourceCondition() {
			//因为 AnyNestedCondition 实现了 ConfigurationCondition，所以要设置 这个属性
			//这个属性在 sholudSkip() 方法中会用到，如果这个属性是 REGISTER_BEAN 的话，在生成 configClass 阶段就不会进行匹配过滤，要等到 loadBeanDefintion 的时候，在进行过滤
			//因为类中的静态内部类，都被 @ConditionalOnProperty 注解，这些注解都是在 configClass 阶段做匹配的，所以要设置为 PARSE_CONFIGURATION
			//如果这里设置为 REGISTER_BEAN，但是内部有应该在 configClass 阶段做匹配的，就不符合整体思想了（这样本应该在 configClass 阶段就做匹配的，延迟到了 loadBeanDefintion 阶段），就可能能出现莫名其妙的问题。
			//进一步思考：继承了 AnyNestedCondition 的子类中，不应该同时存在 configClass 阶段做匹配和在 loadBeanDefintion 阶段匹配的
			super(ConfigurationPhase.PARSE_CONFIGURATION);
		}

		//条件一：是否配置了 spring.datasource.type 属性
		@ConditionalOnProperty(prefix = "spring.datasource", name = "type")
		static class ExplicitType {

		}

		//条件二：项目中是否引入了数据源依赖(如，hikari)
		@Conditional(PooledDataSourceAvailableCondition.class)
		static class PooledDataSourceAvailable {

		}

	}

	/**
	 * {@link Condition} to test if a supported connection pool is available.
	 */
	static class PooledDataSourceAvailableCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			//这个类只是用来传递消息的
			ConditionMessage.Builder message = ConditionMessage.forCondition("PooledDataSource");
			//getDataSourceClassLoader(context)：内部做class.forName来找项目中的相关class，找到了就不为null啦，一般肯定能找到的，在org.springframework.boot:spring-boot-starter-jdbc中就已经引入了 hikariDatabase，而在 spring.boot:mybatis-spring-boot-starter中引入了 jdbc!
			if (DataSourceBuilder.findType(context.getClassLoader()) != null) {
				return ConditionOutcome.match(message.foundExactly("supported DataSource"));
			}
			return ConditionOutcome.noMatch(message.didNotFind("supported DataSource").atAll());
		}

	}

	/**
	 * {@link Condition} to detect when an embedded {@link DataSource} type can be used.
	 * If a pooled {@link DataSource} is available, it will always be preferred to an
	 * {@code EmbeddedDatabase}.
	 */
	//所有的 condition 类都会最终继承 SpringBootCondition，SpringBootCondition 是一个模板类，继承它后，我们只需要实现核心的 getMatchOutCome() 方法来自定义一个 Condition 类了。当这个类被@Conditional 注解引入的时候，最终时候执行这个核心方法来判断是否匹配的
	static class EmbeddedDatabaseCondition extends SpringBootCondition {

		private static final String DATASOURCE_URL_PROPERTY = "spring.datasource.url";

		private final SpringBootCondition pooledCondition = new PooledDataSourceCondition();

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConditionMessage.Builder message = ConditionMessage.forCondition("EmbeddedDataSource");
			if (hasDataSourceUrlProperty(context)) {
				return ConditionOutcome.noMatch(message.because(DATASOURCE_URL_PROPERTY + " is set"));
			}
			// anyMatches() 就是一个 SpringbootCondition 类中的模板方法，意思是：匹配任意一个 pooledCondition 中的条件
			// 这里 pooledCondition 中的条件其实是匹配非内置数据库的条件，这就很奇怪了，为什么不把 匹配非内置数据库的配置放在前面呢？
			if (anyMatches(context, metadata, this.pooledCondition)) {
				return ConditionOutcome.noMatch(message.foundExactly("supported pooled data source"));
			}
			//这里查找了项目中有没有引入 H2，DERBY，HSQL 这3个class，如果没有引入，就返回 null，引入了返回 第一个 type
			EmbeddedDatabaseType type = EmbeddedDatabaseConnection.get(context.getClassLoader()).getType();
			if (type == null) {
				return ConditionOutcome.noMatch(message.didNotFind("embedded database").atAll());
			}
			return ConditionOutcome.match(message.found("embedded database").items(type));
		}

		private boolean hasDataSourceUrlProperty(ConditionContext context) {
			Environment environment = context.getEnvironment();
			if (environment.containsProperty(DATASOURCE_URL_PROPERTY)) {
				try {
					return StringUtils.hasText(environment.getProperty(DATASOURCE_URL_PROPERTY));
				}
				catch (IllegalArgumentException ex) {
					// Ignore unresolvable placeholder errors
				}
			}
			return false;
		}

	}

}
