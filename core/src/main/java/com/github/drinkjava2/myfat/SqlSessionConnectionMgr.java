/**
 *    Copyright 2009-2016 the original author or authors.
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
package com.github.drinkjava2.myfat;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;

import com.github.drinkjava2.jtransactions.ConnectionManager;

/**
 * The primary Java interface for working with MyBatis. Through this interface
 * you can execute commands, get mappers and manage transactions.
 *
 * @author Clinton Begin
 */
public class SqlSessionConnectionMgr implements ConnectionManager {
	private SqlSession session;

	public SqlSessionConnectionMgr(SqlSession session) {
		this.session = session;
	}

	@Override
	public Connection getConnection(DataSource arg0) throws SQLException {
		return session.getConnection();
	}

	@Override
	public boolean isInTransaction(DataSource arg0) {
		return true;
	}

	@Override
	public void releaseConnection(Connection arg0, DataSource arg1) throws SQLException {
		// do nothing
	}
}
