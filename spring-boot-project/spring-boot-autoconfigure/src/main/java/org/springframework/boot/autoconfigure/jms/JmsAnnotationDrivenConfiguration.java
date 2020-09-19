/*
 * Copyright 2012-2019 the original author or authors.
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

package org.springframework.boot.autoconfigure.jms;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerConfigUtils;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.jms.ConnectionFactory;

/**
 * Configuration for Spring 4.1 annotation driven JMS.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
@Configuration(proxyBeanMethods = false)// 声明该类也是配置类
@ConditionalOnClass(EnableJms.class)
class JmsAnnotationDrivenConfiguration {

	// DestinationResolver用于解决JMS目标的策略接口，被JmsTemplate用于将目标名称从简单的字符串解析为实际的Destination实现实例
	private final ObjectProvider<DestinationResolver> destinationResolver;

	// JtaTransactionManager是PlatformTransactionManager的实现类，它提供了Spring的JTA事务管理，也可用于分布式事务的管理
	private final ObjectProvider<JtaTransactionManager> transactionManager;

	// SimpleMessageConverter作为其简单的默认实现，能够处理TextMessages、BytesMessages、MapMessages和ObjectMessages之间的消息转换
	private final ObjectProvider<MessageConverter> messageConverter;

	private final JmsProperties properties;

	JmsAnnotationDrivenConfiguration(ObjectProvider<DestinationResolver> destinationResolver,
			ObjectProvider<JtaTransactionManager> transactionManager, ObjectProvider<MessageConverter> messageConverter,
			JmsProperties properties) {
		this.destinationResolver = destinationResolver;
		this.transactionManager = transactionManager;
		this.messageConverter = messageConverter;
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean
	DefaultJmsListenerContainerFactoryConfigurer jmsListenerContainerFactoryConfigurer() {
		DefaultJmsListenerContainerFactoryConfigurer configurer = new DefaultJmsListenerContainerFactoryConfigurer();
		configurer.setDestinationResolver(this.destinationResolver.getIfUnique());
		configurer.setTransactionManager(this.transactionManager.getIfUnique());
		configurer.setMessageConverter(this.messageConverter.getIfUnique());
		configurer.setJmsProperties(this.properties);
		return configurer;
	}

	@Bean
	@ConditionalOnSingleCandidate(ConnectionFactory.class)
	@ConditionalOnMissingBean(name = "jmsListenerContainerFactory")
	DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
			DefaultJmsListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@Configuration(proxyBeanMethods = false)
	@EnableJms
	@ConditionalOnMissingBean(name = JmsListenerConfigUtils.JMS_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
	static class EnableJmsConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnJndi
	static class JndiConfiguration {

		@Bean
		@ConditionalOnMissingBean(DestinationResolver.class)
		JndiDestinationResolver destinationResolver() {
			JndiDestinationResolver resolver = new JndiDestinationResolver();
			resolver.setFallbackToDynamicDestination(true);
			return resolver;
		}

	}

}
