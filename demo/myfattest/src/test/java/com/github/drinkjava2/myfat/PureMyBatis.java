package com.github.drinkjava2.myfat;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.gctx;

import org.apache.ibatis.mapping.Environment;
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

public class PureMyBatis {

	HikariDataSource ds;

	@Before
	public void init() {
		ds = new HikariDataSource();// DataSource
		ds.setDriverClassName("org.h2.Driver"); // H2 Memory database
		ds.setJdbcUrl("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		ds.setUsername("sa");
		ds.setPassword("");
		SqlBoxContext.setGlobalSqlBoxContext(new SqlBoxContext(ds));// 全局jSqlBox上下文
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

		SqlSession session = sqlSessionFactory.openSession();
		try {
			UserMapper mapper = session.getMapper(UserMapper.class);
			mapper.insertOneUser("1");
			mapper.insertOneUser("2");
			Assert.assertEquals(102, mapper.countAllUser());
			System.out.println(1 / 0);// 测试事务回滚
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		} finally {
			session.close();
		}
		Assert.assertEquals(100, new User().countAll());
	}

}
