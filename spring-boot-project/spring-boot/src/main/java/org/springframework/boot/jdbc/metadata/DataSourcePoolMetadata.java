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

package org.springframework.boot.jdbc.metadata;

import javax.sql.DataSource;

/**
 * Provides access meta-data that is commonly available from most pooled
 * {@link DataSource} implementations.
 *
 * @author Stephane Nicoll
 * @author Artsiom Yudovin
 * @since 2.0.0
 */
public interface DataSourcePoolMetadata {

	/**
	 * Return the usage of the pool as value between 0 and 1 (or -1 if the pool is not
	 * limited).
	 * <ul>
	 * <li>1 means that the maximum number of connections have been allocated</li>
	 * <li>0 means that no connection is currently active</li>
	 * <li>-1 means there is not limit to the number of connections that can be allocated
	 * </li>
	 * </ul>
	 * This may also return {@code null} if the data source does not provide the necessary
	 * information to compute the poll usage.
	 * @return the usage value or {@code null}
	 */
	// 返回当前数据库连接池的情况,返回值在0至1之间(如果连接池没有限制, 值为-1)
	// 返回值1表示：已分配最大连接数
	// 返回值0表示：当前没有连接处于活跃状态
	// 返回值-1表示：可以分配的连接数没有限制
	// 返回null表示：当前数据源不提供必要信息进行计算
	Float getUsage();

	/**
	 * Return the current number of active connections that have been allocated from the
	 * data source or {@code null} if that information is not available.
	 * @return the number of active connections or {@code null}
	 */
	// 返回当前已分配的活跃连接数, 返回null, 则表示该信息不可用
	Integer getActive();

	/**
	 * Return the number of established but idle connections. Can also return {@code null}
	 * if that information is not available.
	 * @return the number of established but idle connections or {@code null}
	 * @since 2.2.0
	 * @see #getActive()
	 */
	default Integer getIdle() {
		return null;
	}

	/**
	 * Return the maximum number of active connections that can be allocated at the same
	 * time or {@code -1} if there is no limit. Can also return {@code null} if that
	 * information is not available.
	 * @return the maximum number of active connections or {@code null}
	 */
	// 返回同时可分配的最大活跃连接数, 返回-1表示不限制, 返回null表示该信息不可用
	Integer getMax();

	/**
	 * Return the minimum number of idle connections in the pool or {@code null} if that
	 * information is not available.
	 * @return the minimum number of active connections or {@code null}
	 */
	// 返回连接池中最小空闲连接数, 返回null表示该信息不可用
	Integer getMin();

	/**
	 * Return the query to use to validate that a connection is valid or {@code null} if
	 * that information is not available.
	 * @return the validation query or {@code null}
	 */
	// 返回查询以验证连接是否有效, 返回null表示该信息不可用
	String getValidationQuery();

	/**
	 * The default auto-commit state of connections created by this pool. If not set
	 * ({@code null}), default is JDBC driver default (If set to null then the
	 * java.sql.Connection.setAutoCommit(boolean) method will not be called.)
	 * @return the default auto-commit state or {@code null}
	 */
	// 连接池创建的连接的默认自动提交状态。如果未将其值设为null, 则默认采用JDBC驱动
	// 如果设置为null, 则方法java.sql.Connection.setAutoCommit(boolean)将不会被调用
	Boolean getDefaultAutoCommit();

}
