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
package org.apache.ibatis.myfat;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;

import com.github.drinkjava2.jtransactions.ConnectionManager;
import com.github.drinkjava2.jtransactions.TxResult;

/**
 * SqlSessionConnectionMgr use the connection from MyBatis's SqlSession
 *
 * @author Yong Zhu
 * @since 1.0.0
 */
public class SqlSessionConnectionMgr implements ConnectionManager {
	private SqlSession session;

	public SqlSessionConnectionMgr(SqlSession session) {
		this.session = session;
	}

	@Override
	public boolean isInTransaction() {
		// will never call this method
		return true;
	}

	@Override
	public void startTransaction() {
		// will never call this method
	}

	@Override
	public void startTransaction(int txIsolationLevel) {
		// will never call this method
	}

	@Override
	public Connection getConnection(Object dsOrHolder) throws SQLException {
		return session.getConnection();
	}

	@Override
	public void releaseConnection(Connection conn, Object dsOrHolder) throws SQLException {
		// will never call this method
	}

	@Override
	public TxResult commitTransaction() throws Exception {
		// will never call this method
		return null;
	}

	@Override
	public TxResult rollbackTransaction() {
		// will never call this method
		return null;
	}

}
