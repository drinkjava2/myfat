/*
 *    Copyright 2016-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.myfat;

import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.github.drinkjava2.jdialects.Dialect;

// This is a MyBatis plugin, referenced this article:
// https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/Interceptor.md

@Intercepts({
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }), })
public class Helper implements Interceptor {
	private static Dialect dialect = Dialect.MySQLDialect;
	public static final Helper instance = new Helper();
	public static ThreadLocal<Object[]> paginData = new ThreadLocal<Object[]>();

	/**
	 * will do paginate for next query
	 * 
	 * @param pageNo
	 *            the page No., started from 0
	 * @param pageSize
	 *            the size for each page
	 * @param dialect
	 *            the jDialect dialect instance
	 */
	public static void pagin(int pageNo, int pageSize) {
		paginData.set(new Object[] { pageNo, pageSize, dialect, false });
	}

	/**
	 * Will do translate for next query
	 */
	public static void trans() {
		paginData.set(new Object[] { 0, 0, dialect, true });
	}

	/**
	 * Will do paginate and translate for next query
	 * 
	 * @param pageNo
	 *            the page No., started from 0
	 * @param pageSize
	 *            the size for each page
	 * @param dialect
	 *            the jDialect dialect instance
	 */
	public static void paginAndTrans(int pageNo, int pageSize) {
		paginData.set(new Object[] { pageNo, pageSize, dialect, true });
	}

	/**
	 * clear pagin and translate setting
	 */
	public static void clear() {
		paginData.remove();
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		int pageNo = -1;
		int pageSize = -1;
		Dialect dialect = null;
		boolean translateFunction = false;
		if (paginData.get() != null) {
			pageNo = (Integer) paginData.get()[0];
			pageSize = (Integer) paginData.get()[1];
			dialect = (Dialect) paginData.get()[2];
			translateFunction = (Boolean) paginData.get()[3];
			paginData.remove();
		}
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		Object parameter = args[1];
		RowBounds rowBounds = (RowBounds) args[2];
		@SuppressWarnings("rawtypes")
		ResultHandler resultHandler = (ResultHandler) args[3];
		Executor executor = (Executor) invocation.getTarget();
		CacheKey cacheKey;
		BoundSql boundSql;
		if (args.length == 4) {
			boundSql = ms.getBoundSql(parameter);
			cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
		} else {
			cacheKey = (CacheKey) args[4];
			boundSql = (BoundSql) args[5];
		}
		if (pageNo != -1) {// if paginInfo exist in threadlocal
			Configuration configuration = ms.getConfiguration();
			String pagedSql;
			if (translateFunction && pageNo == 0)
				pagedSql = dialect.trans(boundSql.getSql());
			else if (translateFunction && pageNo != 0)
				pagedSql = dialect.paginAndTrans(pageNo, pageSize, boundSql.getSql());
			else
				pagedSql = dialect.pagin(pageNo, pageSize, boundSql.getSql());
			BoundSql pageBoundSql = new BoundSql(configuration, pagedSql, boundSql.getParameterMappings(), parameter);
			return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, pageBoundSql);
		} else
			return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	public static Dialect getDialect() {
		return dialect;
	}

	public static void setDialect(Dialect dialect) {
		Helper.dialect = dialect;
	}
}
