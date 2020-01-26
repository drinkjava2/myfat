/**
 *    Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.session;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.myfat.SqlSessionConnectionMgr;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import com.github.drinkjava2.jbeanbox.ReflectionUtils;
import com.github.drinkjava2.jsqlbox.DbContext;
import com.github.drinkjava2.jsqlbox.DbException;
import com.github.drinkjava2.jsqlbox.entitynet.EntityNet;

/**
 * The primary Java interface for working with MyBatis.
 * Through this interface you can execute commands, get mappers and manage transactions.
 *
 * @author Clinton Begin
 */
public interface SqlSession extends Closeable {

  /**
   * Retrieve a single row mapped from the statement key.
   * @param <T> the returned object type
   * @param statement
   * @return Mapped object
   */
  <T> T selectOne(String statement);

  /**
   * Retrieve a single row mapped from the statement key and parameter.
   * @param <T> the returned object type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return Mapped object
   */
  <T> T selectOne(String statement, Object parameter);

  /**
   * Retrieve a list of mapped objects from the statement key and parameter.
   * @param <E> the returned list element type
   * @param statement Unique identifier matching the statement to use.
   * @return List of mapped object
   */
  <E> List<E> selectList(String statement);

  /**
   * Retrieve a list of mapped objects from the statement key and parameter.
   * @param <E> the returned list element type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return List of mapped object
   */
  <E> List<E> selectList(String statement, Object parameter);

  /**
   * Retrieve a list of mapped objects from the statement key and parameter,
   * within the specified row bounds.
   * @param <E> the returned list element type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param rowBounds  Bounds to limit object retrieval
   * @return List of mapped object
   */
  <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

  /**
   * The selectMap is a special case in that it is designed to convert a list
   * of results into a Map based on one of the properties in the resulting
   * objects.
   * Eg. Return a of Map[Integer,Author] for selectMap("selectAuthors","id")
   * @param <K> the returned Map keys type
   * @param <V> the returned Map values type
   * @param statement Unique identifier matching the statement to use.
   * @param mapKey The property to use as key for each value in the list.
   * @return Map containing key pair data.
   */
  <K, V> Map<K, V> selectMap(String statement, String mapKey);

  /**
   * The selectMap is a special case in that it is designed to convert a list
   * of results into a Map based on one of the properties in the resulting
   * objects.
   * @param <K> the returned Map keys type
   * @param <V> the returned Map values type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param mapKey The property to use as key for each value in the list.
   * @return Map containing key pair data.
   */
  <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);

  /**
   * The selectMap is a special case in that it is designed to convert a list
   * of results into a Map based on one of the properties in the resulting
   * objects.
   * @param <K> the returned Map keys type
   * @param <V> the returned Map values type
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param mapKey The property to use as key for each value in the list.
   * @param rowBounds  Bounds to limit object retrieval
   * @return Map containing key pair data.
   */
  <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);

  /**
   * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
   * @param <T> the returned cursor element type.
   * @param statement Unique identifier matching the statement to use.
   * @return Cursor of mapped objects
   */
  <T> Cursor<T> selectCursor(String statement);

  /**
   * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
   * @param <T> the returned cursor element type.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @return Cursor of mapped objects
   */
  <T> Cursor<T> selectCursor(String statement, Object parameter);

  /**
   * A Cursor offers the same results as a List, except it fetches data lazily using an Iterator.
   * @param <T> the returned cursor element type.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param rowBounds  Bounds to limit object retrieval
   * @return Cursor of mapped objects
   */
  <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

  /**
   * Retrieve a single row mapped from the statement key and parameter
   * using a {@code ResultHandler}.
   * @param statement Unique identifier matching the statement to use.
   * @param parameter A parameter object to pass to the statement.
   * @param handler ResultHandler that will handle each retrieved row
   */
  void select(String statement, Object parameter, ResultHandler handler);

  /**
   * Retrieve a single row mapped from the statement
   * using a {@code ResultHandler}.
   * @param statement Unique identifier matching the statement to use.
   * @param handler ResultHandler that will handle each retrieved row
   */
  void select(String statement, ResultHandler handler);

  /**
   * Retrieve a single row mapped from the statement key and parameter
   * using a {@code ResultHandler} and {@code RowBounds}.
   * @param statement Unique identifier matching the statement to use.
   * @param rowBounds RowBound instance to limit the query results
   * @param handler ResultHandler that will handle each retrieved row
   */
  void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler);

  /**
   * Execute an insert statement.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the insert.
   */
  int insert(String statement);

  /**
   * Execute an insert statement with the given parameter object. Any generated
   * autoincrement values or selectKey entries will modify the given parameter
   * object properties. Only the number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the insert.
   */
  int insert(String statement, Object parameter);

  /**
   * Execute an update statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the update.
   */
  int update(String statement);

  /**
   * Execute an update statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the update.
   */
  int update(String statement, Object parameter);

  /**
   * Execute a delete statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @return int The number of rows affected by the delete.
   */
  int delete(String statement);

  /**
   * Execute a delete statement. The number of rows affected will be returned.
   * @param statement Unique identifier matching the statement to execute.
   * @param parameter A parameter object to pass to the statement.
   * @return int The number of rows affected by the delete.
   */
  int delete(String statement, Object parameter);

  /**
   * Flushes batch statements and commits database connection.
   * Note that database connection will not be committed if no updates/deletes/inserts were called.
   * To force the commit call {@link SqlSession#commit(boolean)}
   */
  void commit();

  /**
   * Flushes batch statements and commits database connection.
   * @param force forces connection commit
   */
  void commit(boolean force);

  /**
   * Discards pending batch statements and rolls database connection back.
   * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
   * To force the rollback call {@link SqlSession#rollback(boolean)}
   */
  void rollback();

  /**
   * Discards pending batch statements and rolls database connection back.
   * Note that database connection will not be rolled back if no updates/deletes/inserts were called.
   * @param force forces connection rollback
   */
  void rollback(boolean force);

  /**
   * Flushes batch statements.
   * @return BatchResult list of updated records
   * @since 3.0.6
   */
  List<BatchResult> flushStatements();

  /**
   * Closes the session.
   */
  @Override
  void close();

  /**
   * Clears local session cache.
   */
  void clearCache();

  /**
   * Retrieves current configuration.
   * @return Configuration
   */
  Configuration getConfiguration();

  /**
   * Retrieves a mapper.
   * @param <T> the mapper type
   * @param type Mapper interface class
   * @return a mapper bound to this SqlSession
   */
  <T> T getMapper(Class<T> type);

  /**
   * Retrieves inner database connection.
   * @return Connection
   */
  Connection getConnection();

	// ===========Below are added by YZ for MyFat plugin =======
	/**
	 * Return a new DbContext based on current SqlSession's dataSource
	 */
	public default DbContext qryCtx() {
		Configuration cfg = getConfiguration();
		Environment env = cfg.getEnvironment();
		DataSource ds = env.getDataSource();
		DbContext ctx = new DbContext(ds);
		SqlSessionConnectionMgr cm = new SqlSessionConnectionMgr(this);
		ctx.setConnectionManager(cm);
		return ctx;
	}
	
	/**
	 * Return a new DbContext based on current SqlSession's dataSource, and mark sqlsession
	 *  "dirty" to make it start a transaction
	 */
	public default DbContext ctx() { 	  
		if (this instanceof DefaultSqlSession) { 
			Field f = ReflectionUtils.findField(this.getClass(), "dirty");
			if (f == null)
				throw new DbException("Can not access SqlSession, target:" +this.getClass());
			f.setAccessible(true);
			ReflectionUtils.setField(f, this, true);
		}
		return qryCtx(); 
	}
  
	//@formatter:off 
	// p series methods from jSqlBox
	public default <T> T pQuery(Object... items) {return qryCtx().pQuery(items);}
	public default <T> T pQueryForObject(Object... items) {return qryCtx().pQueryForObject(items);}
	public default long pQueryForLongValue(Object... items) {return qryCtx().pQueryForLongValue(items);}
	public default int pQueryForIntValue(Object... items) {return qryCtx().pQueryForIntValue(items);}
	public default String pQueryForString(Object... items) {return qryCtx().pQueryForString(items);}
	public default List<Map<String, Object>> pQueryForMapList(Object... items) {return qryCtx().pQueryForMapList(items);}
	public default int pUpdate(Object... items) {return ctx().pUpdate(items);}
	public default <T> T pInsert(Object... items) {return ctx().pInsert(items);}
	public default <T> T pExecute(Object... items) {return ctx().pExecute(items); }  
	public default <T> List<T> pQueryForEntityList(Object... items) {return qryCtx().pQueryForEntityList(items);} 
	
	// i series methods from jSqlBox
	public default <T> T iQuery(Object... items) {return  qryCtx().iQuery(items);}
	public default <T> T iQueryForObject(Object... items) {return qryCtx().iQueryForObject(items);}
	public default long iQueryForLongValue(Object... items) {return qryCtx().iQueryForLongValue(items);}
	public default int iQueryForIntValue(Object... items) {return qryCtx().iQueryForIntValue(items);}
	public default String iQueryForString(Object... items) {return qryCtx().iQueryForString(items);}
	public default List<Map<String, Object>> iQueryForMapList(Object... items) {return qryCtx().iQueryForMapList(items);}
	public default int iUpdate(Object... items) {return ctx().iUpdate(items);}
	public default <T> T iInsert(Object... items) {return ctx().iInsert(items);}
	public default <T> T iExecute(Object... items) {return ctx().iExecute(items); }
	public default <T> List<T> iQueryForEntityList(Object... items) {return qryCtx().iQueryForEntityList(items);}
	   
	// n series methods from jSqlBox
	public default <T> T nQuery(Connection conn, ResultSetHandler<T> rsh, String sql, Object... items) {return qryCtx().nQuery(conn, rsh, sql, items);}
	public default <T> T nQueryForObject(Connection conn, String sql, Object... items) {return qryCtx().nQueryForObject(conn, sql, items);}
	public default String nQueryForString(Connection conn, String sql, Object... items) {return qryCtx().nQueryForString(conn, sql, items);}
	public default long nQueryForLongValue(Connection conn, String sql, Object... items) {return qryCtx().nQueryForLongValue(conn, sql, items);}
	public default int nQueryForIntValue(Connection conn, String sql, Object... items) {return qryCtx().nQueryForIntValue(conn, sql, items);}
	public default List<Map<String, Object>> nQueryForMapList(Connection conn, String sql, Object... items) {return qryCtx().nQueryForMapList(conn, sql, items);}
	public default int nUpdate(Connection conn, String sql, Object... items) {return ctx().nUpdate(conn, sql, items);}
	public default <T> T nInsert(Connection conn, ResultSetHandler<T> rsh, String sql, Object... items) {return ctx().nInsert(conn, rsh, sql, items);}
	public default int nExecute(Connection conn, String sql, Object... items) {return ctx().nExecute(conn, sql, items);}
	public default <T> List<T> nExecute(Connection conn, ResultSetHandler<T> rsh, String sql, Object... items) {return ctx().nExecute(conn, rsh, sql, items);}
	public default <T> T nQuery(ResultSetHandler<T> rsh, String sql, Object... items) {return qryCtx().nQuery(rsh, sql, items);}
	public default <T> T nQueryForObject(String sql, Object... items) {return qryCtx().nQueryForObject(sql, items);}
	public default String nQueryForString(String sql, Object... items) {return qryCtx().nQueryForString(sql, items);}
	public default long nQueryForLongValue(String sql, Object... items) {return qryCtx().nQueryForLongValue(sql, items);}
	public default int nQueryForIntValue(String sql, Object... items) {return qryCtx().nQueryForIntValue(sql, items);}
	public default List<Map<String, Object>> nQueryForMapList(String sql, Object... items) {return qryCtx().nQueryForMapList(sql, items);}
	public default int nUpdate(String sql, Object... items) {return ctx().nUpdate(sql, items);}
	public default <T> T nInsert(@SuppressWarnings("rawtypes") ResultSetHandler rsh, String sql, Object... items) {return ctx().nInsert(rsh, sql, items);}
	public default int nExecute(String sql, Object... items) {return ctx().nExecute(sql, items);}
	public default <T> List<T> nExecute(@SuppressWarnings("rawtypes") ResultSetHandler rsh, String sql, Object... items) {return ctx().nExecute(rsh, sql, items);}
		
	// t series methods from jSqlBox
	public default <T> T tQuery(Object... items) {return qryCtx().tQuery(items);}
	public default <T> T tQueryForObject(Object... items) {return qryCtx().tQueryForObject(items);}
	public default long tQueryForLongValue(Object... items) {return qryCtx().tQueryForLongValue(items);}
	public default int tQueryForIntValue(Object... items) {return qryCtx().tQueryForIntValue(items);}
	public default String tQueryForString(Object... items) {return qryCtx().tQueryForString(items);}
	public default List<Map<String, Object>> tQueryForMapList(Object... items) {return qryCtx().tQueryForMapList(items);}
	public default int tUpdate(Object... items) {return ctx().tUpdate(items);}
	public default <T> T tInsert(Object... items) {return ctx().tInsert(items);}
	public default <T> T tExecute(Object... items) {return ctx().tExecute(items);}
	public default <T> List<T> tQueryForEntityList(Class<T> entityClass, Object... items) {return qryCtx().tQueryForEntityList(entityClass, items); }
 
  	// e series methods from jSqlBox
	public default <T> List<T> eFindAll(Class<T> entityClass, Object... items) {return qryCtx().eFindAll(entityClass, items);}
	public default <T> List<T> eFindBySample(Object sampleBean, Object... items) {return qryCtx().eFindBySample(sampleBean, items);}
	public default <T> List<T> eFindBySQL(Object... items) {return qryCtx().eFindBySQL(items);}   
	public default <T> T eInsert(T entity, Object... items) {return ctx().eInsert(entity, items);} 
	public default <T> T eLoad(T entity, Object... items) {return qryCtx().eLoad(entity, items);} 
	public default <T> T eLoadById(Class<T> entityClass, Object entityId, Object... items) {return qryCtx().eLoadById(entityClass, entityId, items);}
	public default <T> T eLoadByIdTry(Class<T> entityClass, Object entityId, Object... items) {return qryCtx().eLoadByIdTry(entityClass, entityId, items);}
	public default <T> T eLoadBySQL(Object... items) {return qryCtx().eLoadBySQL(items);}	
	public default <T> T eUpdate(Object entity, Object... items) {return ctx().eUpdate(entity, items);}
	public default boolean eExist(Object entity, Object... items) {return qryCtx().eExist(entity, items);}
	public default boolean eExistById(Class<?> entityClass, Object id, Object... items) {return qryCtx().eExistById(entityClass, id, items);}
	public default int eCountAll(Class<?> entityClass, Object... items) {return qryCtx().eCountAll(entityClass, items);}
	public default int eDeleteByIdTry(Class<?> entityClass, Object id, Object... items) {return ctx().eDeleteByIdTry(entityClass, id, items);}
	public default int eDeleteTry(Object entity, Object... items) {return ctx().eDeleteTry(entity, items);}
	public default int eLoadTry(Object entity, Object... items) {return qryCtx().eLoadTry(entity, items);}
	public default int eUpdateTry(Object entity, Object... items) {return ctx().eUpdateTry(entity, items);}
	public default void eDelete(Object entity, Object... items) { ctx().eDelete(entity, items);}
	public default void eDeleteById(Class<?> entityClass, Object id, Object... items) {ctx().eDeleteById(entityClass, id, items);}
	public default EntityNet eAutoNet(Class<?>... entityClass) {return  qryCtx().autoNet(entityClass);}
	public default <T> T eFindRelatedOne(Object entity, Object... sqlItems) {return  qryCtx().eFindRelatedOne(entity, sqlItems);}
	public default <T> List<T> eFindRelatedList(Object entityOrIterable, Object... sqlItems) {return  qryCtx().eFindRelatedList(entityOrIterable, sqlItems);}
	public default <T> Set<T> eFindRelatedSet(Object entity, Object... sqlItems) {return  qryCtx().eFindRelatedSet(entity, sqlItems);}
	public default <T> Map<Object, T> eFindRelatedMap(Object entity, Object... sqlItems) {return  qryCtx().eFindRelatedMap(entity, sqlItems);}
	
}
