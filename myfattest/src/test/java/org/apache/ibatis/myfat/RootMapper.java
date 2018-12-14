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

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import com.github.drinkjava2.jsqlbox.SqlBoxContext;
import com.github.drinkjava2.jsqlbox.entitynet.EntityNet;

/**
 * @author Yong Zhu
 */
public interface RootMapper<M> {

	public SqlSession getSqlSession();
	
	public Class<M> getMapperEntityClass(); 
	

	public default SqlBoxContext ctx() {
		SqlSession session = getSqlSession();
		Configuration cfg = session.getConfiguration();
		Environment env = cfg.getEnvironment();
		DataSource ds = env.getDataSource();
		SqlBoxContext ctx = new SqlBoxContext(ds);
		SqlSessionConnectionMgr cm = new SqlSessionConnectionMgr(session);
		ctx.setConnectionManager(cm);
		return ctx;
	} 

	//@formatter:off
 
	// p series methods from jSqlBox
	public default <T> T pQuery(Object... items) {return ctx().pQuery(items);}
	public default <T> T pQueryForObject(Object... items) {return ctx().pQueryForObject(items);}
	public default long pQueryForLongValue(Object... items) {return ctx().pQueryForLongValue(items);}
	public default String pQueryForString(Object... items) {return ctx().pQueryForString(items);}
	public default List<Map<String, Object>> pQueryForMapList(Object... items) {return ctx().pQueryForMapList(items);}
	public default int pUpdate(Object... items) {return ctx().pUpdate(items);}
	public default <T> T pInsert(Object... items) {return ctx().pInsert(items);}
	public default <T> T pExecute(Object... items) {return ctx().pExecute(items); }  
	public default <T> List<T> pQueryForEntityList(Object... items) {return ctx().pQueryForEntityList(items);} 
	
	// i series methods from jSqlBox
	public default <T> T iQuery(Object... items) {return  ctx().iQuery(items);}
	public default <T> T iQueryForObject(Object... items) {return ctx().iQueryForObject(items);}
	public default long iQueryForLongValue(Object... items) {return ctx().iQueryForLongValue(items);}
	public default String iQueryForString(Object... items) {return ctx().iQueryForString(items);}
	public default List<Map<String, Object>> iQueryForMapList(Object... items) {return ctx().iQueryForMapList(items);}
	public default int iUpdate(Object... items) {return ctx().iUpdate(items);}
	public default <T> T iInsert(Object... items) {return ctx().iInsert(items);}
	public default <T> T iExecute(Object... items) {return ctx().iExecute(items); }
	public default <T> List<T> iQueryForEntityList(Object... items) {return ctx().iQueryForEntityList(items);}
	   
	// n series methods from jSqlBox
	public default <T> T nQuery(Connection conn, ResultSetHandler<T> rsh, String sql, Object... items) {return ctx().nQuery(conn, rsh, sql, items);}
	public default <T> T nQueryForObject(Connection conn, String sql, Object... items) {return ctx().nQueryForObject(conn, sql, items);}
	public default String nQueryForString(Connection conn, String sql, Object... items) {return ctx().nQueryForString(conn, sql, items);}
	public default long nQueryForLongValue(Connection conn, String sql, Object... items) {return ctx().nQueryForLongValue(conn, sql, items);}
	public default List<Map<String, Object>> nQueryForMapList(Connection conn, String sql, Object... items) {return ctx().nQueryForMapList(conn, sql, items);}
	public default int nUpdate(Connection conn, String sql, Object... items) {return ctx().nUpdate(conn, sql, items);}
	public default <T> T nInsert(Connection conn, ResultSetHandler<T> rsh, String sql, Object... items) {return ctx().nInsert(conn, rsh, sql, items);}
	public default int nExecute(Connection conn, String sql, Object... items) {return ctx().nExecute(conn, sql, items);}
	public default <T> List<T> nExecute(Connection conn, ResultSetHandler<T> rsh, String sql, Object... items) {return ctx().nExecute(conn, rsh, sql, items);}
	public default <T> T nQuery(ResultSetHandler<T> rsh, String sql, Object... items) {return ctx().nQuery(rsh, sql, items);}
	public default <T> T nQueryForObject(String sql, Object... items) {return ctx().nQueryForObject(sql, items);}
	public default String nQueryForString(String sql, Object... items) {return ctx().nQueryForString(sql, items);}
	public default long nQueryForLongValue(String sql, Object... items) {return ctx().nQueryForLongValue(sql, items);}
	public default List<Map<String, Object>> nQueryForMapList(String sql, Object... items) {return ctx().nQueryForMapList(sql, items);}
	public default int nUpdate(String sql, Object... items) {return ctx().nUpdate(sql, items);}
	public default <T> T nInsert(@SuppressWarnings("rawtypes") ResultSetHandler rsh, String sql, Object... items) {return ctx().nInsert(rsh, sql, items);}
	public default int nExecute(String sql, Object... items) {return ctx().nExecute(sql, items);}
	public default <T> List<T> nExecute(@SuppressWarnings("rawtypes") ResultSetHandler rsh, String sql, Object... items) {return ctx().nExecute(rsh, sql, items);}
		
	// t series methods from jSqlBox
	public default <T> T tQuery(Object... items) {return ctx().tQuery(items);}
	public default <T> T tQueryForObject(Object... items) {return ctx().tQueryForObject(items);}
	public default long tQueryForLongValue(Object... items) {return ctx().tQueryForLongValue(items);}
	public default String tQueryForString(Object... items) {return ctx().tQueryForString(items);}
	public default List<Map<String, Object>> tQueryForMapList(Object... items) {return ctx().tQueryForMapList(items);}
	public default int tUpdate(Object... items) {return ctx().tUpdate(items);}
	public default <T> T tInsert(Object... items) {return ctx().tInsert(items);}
	public default <T> T tExecute(Object... items) {return ctx().tExecute(items);}
	public default <T> List<T> tQueryForEntityList(Class<T> entityClass, Object... items) {return ctx().tQueryForEntityList(entityClass, items); }
 
	//e series methods from jSqlBox
	public default <T> List<T> eFindAll(Class<T> entityClass, Object... items) {return ctx().eFindAll(entityClass, items);}
	public default <T> List<T> eFindBySample(Object sampleBean, Object... items) {return ctx().eFindBySample(sampleBean, items);}
	public default <T> List<T> eFindBySQL(Object... items) {return ctx().eFindBySQL(items);}   
	public default <T> T eInsert(T entity, Object... items) {return ctx().eInsert(entity, items);} 
	public default <T> T eLoad(T entity, Object... items) {return ctx().eLoad(entity, items);} 
	public default <T> T eLoadById(Class<T> entityClass, Object entityId, Object... items) {return ctx().eLoadById(entityClass, entityId, items);}
	public default <T> T eLoadByIdTry(Class<T> entityClass, Object entityId, Object... items) {return ctx().eLoadByIdTry(entityClass, entityId, items);}
	public default <T> T eLoadBySQL(Object... items) {return ctx().eLoadBySQL(items);}	
	public default <T> T eUpdate(Object entity, Object... items) {return ctx().eUpdate(entity, items);}
	public default boolean eExist(Object entity, Object... items) {return ctx().eExist(entity, items);}
	public default boolean eExistById(Class<?> entityClass, Object id, Object... items) {return ctx().eExistById(entityClass, id, items);}
	public default int eCountAll(Class<?> entityClass, Object... items) {return ctx().eCountAll(entityClass, items);}
	public default int eDeleteByIdTry(Class<?> entityClass, Object id, Object... items) {return ctx().eDeleteByIdTry(entityClass, id, items);}
	public default int eDeleteTry(Object entity, Object... items) {return ctx().eDeleteTry(entity, items);}
	public default int eLoadTry(Object entity, Object... items) {return ctx().eLoadTry(entity, items);}
	public default int eUpdateTry(Object entity, Object... items) {return ctx().eUpdateTry(entity, items);}
	public default void eDelete(Object entity, Object... items) { ctx().eDelete(entity, items);}
	public default void eDeleteById(Class<?> entityClass, Object id, Object... items) {ctx().eDeleteById(entityClass, id, items);}
	public default EntityNet eAutoNet(Class<?>... entityClass) {return  ctx().autoNet(entityClass);}
	public default <T> T eFindRelatedOne(Object entity, Object... sqlItems) {return  ctx().eFindRelatedOne(entity, sqlItems);}
	public default <T> List<T> eFindRelatedList(Object entityOrIterable, Object... sqlItems) {return  ctx().eFindRelatedList(entityOrIterable, sqlItems);}
	public default <T> Set<T> eFindRelatedSet(Object entity, Object... sqlItems) {return  ctx().eFindRelatedSet(entity, sqlItems);}
	public default <T> Map<Object, T> eFindRelatedMap(Object entity, Object... sqlItems) {return  ctx().eFindRelatedMap(entity, sqlItems);}
		
  	//activeRecord series methods from jSqlBox
	public default boolean existById(  Object id, Object... items) {return ctx().eExistById(getMapperEntityClass(), id, items);}
	public default int countAll(  Object... items) {return ctx().eCountAll(getMapperEntityClass(), items);}
	public default int deleteByIdTry(  Object id, Object... items) {return ctx().eDeleteByIdTry(getMapperEntityClass(), id, items);}
	public default void deleteById(  Object id, Object... items) {ctx().eDeleteById(getMapperEntityClass(), id, items);}
	public default List<M> findAll(Object... items) {return ctx().eFindAll(getMapperEntityClass(), items);}
	public default List<M> findBySQL(Object... items) {return ctx().eFindBySQL(getMapperEntityClass(), items);}
	public default M loadById(  Object entityId, Object... items) {return ctx().eLoadById(getMapperEntityClass(), entityId, items);}
	public default M loadByIdTry(  Object entityId, Object... items) {return ctx().eLoadByIdTry(getMapperEntityClass(), entityId, items);}  
	
}
