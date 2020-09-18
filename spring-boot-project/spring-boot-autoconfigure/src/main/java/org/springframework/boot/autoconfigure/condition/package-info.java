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

/**
 * {@code @Condition} annotations and supporting classes.
 */
package org.springframework.boot.autoconfigure.condition;

/**
 * ·@ConditionalOnBean：在容器中有指定Bean的条件下。
 * ·@ConditionalOnClass：在classpath类路径下有指定类的条件下。
 * ·@ConditionalOnCloudPlatform：当指定的云平台处于active状态时。
 * ·@ConditionalOnExpression：基于SpEL表达式的条件判断。
 * ·@ConditionalOnJava：基于JVM版本作为判断条件。
 * ·@ConditionalOnJndi：在JNDI存在的条件下查找指定的位置。
 * ·@ConditionalOnMissingBean：当容器里没有指定Bean的条件时。
 * ·@ConditionalOnMissingClass：当类路径下没有指定类的条件时。
 * ·@ConditionalOnNotWebApplication：在项目不是一个Web项目的条件下。
 * ·@ConditionalOnProperty：在指定的属性有指定值的条件下。
 * ·@ConditionalOnResource：类路径是否有指定的值。
 * ·@ConditionalOnSingleCandidate：当指定的Bean在容器中只有一个或者有多个但是指定了首选的Bean时。
 * ·@ConditionalOnWebApplication：在项目是一个Web项目的条件下。
 */