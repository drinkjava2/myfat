package com.github.drinkjava2.myfat;

import static com.github.drinkjava2.jdbpro.JDBPRO.param;
import static com.github.drinkjava2.jdbpro.JDBPRO.ques;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.gctx;

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
import com.github.drinkjava2.myfat.entity.User;
import com.github.drinkjava2.myfat.entity.UserMapper;
import com.zaxxer.hikari.HikariDataSource;

public class ManualTxTest {

	HikariDataSource ds;

	@Before
	public void init() {
		ds = new HikariDataSource();// DataSource
		ds.setDriverClassName("org.h2.Driver"); // H2 Memory database
		ds.setJdbcUrl("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		ds.setUsername("sa");
		ds.setPassword("");
		SqlBoxContext.setGlobalSqlBoxContext(new SqlBoxContext(ds));// 全局jSqlBox上下文
		gctx().setAllowShowSQL(true);
		String[] ddlArray = gctx().toDropAndCreateDDL(User.class);
		for (String ddl : ddlArray)
			gctx().quiteExecute(ddl);// 建空表
		for (int i = 1; i <= 100; i++)
			new User().putField("firstName", "Foo" + i, "lastName", "Bar" + i, "age", i).insert();// 测试数据
	}

	@After
	public void clean() {
		ds.close();
	}

	@Test
	public void TxTest() {
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("demo", transactionFactory, ds);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(UserMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		// 以上4行完成MyBatis的配置

		SqlSession session = sqlSessionFactory.openSession(false);
		try {
			 
			UserMapper mapper = session.getMapper(UserMapper.class);
			SqlBoxContext ctx = mapper.ctx();

			// MyBatis的SqlSession有了增强的SQL方法，jSqlBox中的pinte系列方法可以用
			Assert.assertEquals(50, session.iQueryForLongValue("select count(*) from user_tb where age>", ques(50),
					" and age <= ", ques(200)));

			ctx.setAllowShowSQL(true);
			ctx.eCountAll(User.class, " where 1<>0");// TODO: debug log did not show

			// mapper也有增强的SQL方法了，pintea系列方法都可以用
			Assert.assertEquals(100, mapper.countAll(" where age>?", param(0), " and age<", ques(200)));

			// 无侵入的分页
			Helper.pagin(2, 10);
			try {
				List<User> users = mapper.getOlderThan(50);
				Assert.assertEquals(10, users.size());
			} finally {
				Helper.clear();
			}

			// ActiveRecord写法
			session.getConfiguration().getEnvironment().getTransactionFactory().
			ctx.iExecute("insert into user_tb (id) values('a')");
			new User().insert(ctx);
			// mapper.insertOneUser("1");
			session.clearCache();
			System.out.println(1 / 0);// 测试事务回滚
		} catch (Exception e) {
			session.rollback();
			System.out.println("Exception:" + e.getMessage());
		} finally {
			session.close();
		}
		Assert.assertEquals(100, new User().countAll());
	}

}
