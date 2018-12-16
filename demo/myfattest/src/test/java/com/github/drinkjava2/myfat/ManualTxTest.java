package com.github.drinkjava2.myfat;

import static com.github.drinkjava2.jdbpro.JDBPRO.param;
import static com.github.drinkjava2.jdbpro.JDBPRO.ques;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.gctx;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.tail;

import java.util.List;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.myfat.Helper;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.drinkjava2.jsqlbox.SqlBoxContext;
import com.github.drinkjava2.jsqlbox.Tail;
import com.github.drinkjava2.myfat.entity.User;
import com.github.drinkjava2.myfat.entity.UserMapper;
import com.zaxxer.hikari.HikariDataSource;

public class ManualTxTest {

	HikariDataSource dataSource;

	@Before
	public void init() {
		dataSource = new HikariDataSource();// DataSource
		dataSource.setDriverClassName("org.h2.Driver"); // H2 Memory database
		dataSource.setJdbcUrl("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		SqlBoxContext.setGlobalSqlBoxContext(new SqlBoxContext(dataSource));// 全局jSqlBox上下文
		String[] ddlArray = gctx().toDropAndCreateDDL(User.class);
		for (String ddl : ddlArray)
			gctx().quiteExecute(ddl);// 建空表
		for (int i = 1; i <= 100; i++)
			new User().putField("firstName", "Foo" + i, "lastName", "Bar" + i, "age", i).insert();// 测试数据
	}

	@After
	public void clean() {
		dataSource.close();
	}

	@Test
	public void TxTest() {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("demo", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(UserMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		// 以上4行完成MyBatis的配置

		SqlSession session = sqlSessionFactory.openSession(false);
		try { 
			// SqlSession有了增强的SQL方法，jSqlBox中的pinte系列方法可用
			Assert.assertEquals(50, session.iQueryForLongValue("select count(*) from user_tb where age>", ques(50),
					" and age <= ", ques(200)));

			Assert.assertEquals(100, session.eCountAll(User.class, " where age>0"));

			UserMapper mapper = session.getMapper(UserMapper.class);
			// mapper也有增强的pintea系方法了，mapper继承于RootMapper<T>
			Assert.assertEquals(100, mapper.aCountAll(" where age>?", param(0), " and age<?", param(200)));
			
			Helper.pagin(2, 15);// 无侵入的分页
			try {
				List<User> users = mapper.getUsersOlderThan(50);
				Assert.assertEquals(15, users.size());
			} finally {
				Helper.clear();
			}

			new User().insert(session.ctx());// ActiveRecord写法
			new Tail().putTail("id", "1").insert(session.ctx(), tail("user_tb")); // Tail相当于jFinal中的Record
			session.ctx().iExecute("insert into user_tb (id) values(?)", param('a'));
			session.iExecute("insert into user_tb (id) values(?)", param('b'));
			mapper.iExecute("insert into user_tb (id) values(?)", param('c'));
			mapper.insertOneUser("d");
			Assert.assertEquals(106, new User().countAll(session.ctx()));

			System.out.println(1 / 0);// 测试事务回滚
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		} finally {
			session.close();
		}
		Assert.assertEquals(100, new User().countAll());
	}

}
