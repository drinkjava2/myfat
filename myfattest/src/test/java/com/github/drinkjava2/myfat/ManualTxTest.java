package com.github.drinkjava2.myfat;

import static com.github.drinkjava2.jdbpro.JDBPRO.param;
import static com.github.drinkjava2.jdbpro.JDBPRO.ques;
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

	SqlSessionFactory sqlSessionFactory;
	HikariDataSource dataSource;

	@Before
	public void init() {
		SqlBoxContext.setGlobalNextAllowShowSql(true);
		dataSource = new HikariDataSource();// DataSource
		// H2 is a memory database
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setJdbcUrl("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		dataSource.setUsername("sa");
		dataSource.setPassword("");

		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("demo", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(UserMapper.class);
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		SqlSession session = sqlSessionFactory.openSession();
		try {
			String[] ddlArray = session.ctx().toDropAndCreateDDL(User.class);
			for (String ddl : ddlArray)
				session.ctx().quiteExecute(ddl);
			for (int i = 1; i <= 100; i++)
				new User().putField("firstName", "Foo" + i, "lastName", "Bar" + i, "age", i).insert(session.ctx());
		} finally {
			session.close();
		}
	}

	@After
	public void clean() {
		dataSource.close();
	}

	@Test
	public void DemoTest() {
		SqlSession session = sqlSessionFactory.openSession();
		try {
			// MyBatis的Session有了增强的SQL方法，jSqlBox中的pinte系列方法可以用
			Assert.assertEquals(50, session.iQueryForLongValue("select count(*) from users where age>", ques(50),
					" and age <= ", ques(100)));
			List<User> users;
			UserMapper mapper = session.getMapper(UserMapper.class);

			// mapper也有增强的SQL方法了，pintea系列方法都可以用
			Assert.assertEquals(50, mapper.eCountAll(User.class, " where age>?", param(50)));
			Assert.assertEquals(50, mapper.countAll(" where age>", ques(50)));

			// 对任意MyBatis查询都可以分页了，无侵入性
			Helper.pagin(2, 10); // 或paginAndTrans(2,10)还可以进行跨数据库的函数翻译
			users = mapper.getOlderThan(50);
			Assert.assertEquals(10, users.size());
			for (User u : users)
				System.out.println("UserName=" + u.getFirstName() + "," + u.getLastName() + ", age=" + u.getAge());

			// Tail也出现在mapper中了
			Assert.assertEquals(100, mapper.eCountAll(Tail.class, tail("users")));

			// ActiveRecord写法
			new User().putField("firstName", "foo").insert(session.ctx());
			Assert.assertEquals(101, new User().countAll(session.ctx()));
		} finally {
			session.close();
		}
	}

	@Test
	public void TxTest() {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			session.ctx().setAllowShowSQL(true);
			// MyBatis的Session有了增强的SQL方法，jSqlBox中的pinte系列方法可以用
			Assert.assertEquals(50, session.iQueryForLongValue("select count(*) from users where age>", ques(50),
					" and age <= ", ques(200)));
			List<User> users;
			UserMapper mapper = session.getMapper(UserMapper.class);

			// mapper也有增强的SQL方法了，pintea系列方法都可以用
			Assert.assertEquals(50, mapper.eCountAll(User.class, " where age>?", param(50)));
			Assert.assertEquals(50, mapper.countAll(" where age>", ques(50)));

			// 分页
			Helper.pagin(2, 10);
			users = mapper.getOlderThan(50);
			Assert.assertEquals(10, users.size());
			for (User u : users)
				System.out.println("UserName=" + u.getFirstName() + "," + u.getLastName() + ", age=" + u.getAge());

			// Tail也出现在mapper中了
			Assert.assertEquals(100, mapper.eCountAll(Tail.class, tail("users")));

 
			// ActiveRecord写法
			new User().putField("firstName", "Foo").insert(mapper.ctx());
			new User().putField("firstName", "Bar").insert(mapper.ctx());
			new User().putField("firstName", "Bar").insert(mapper.ctx());
			new User().putField("firstName", "Bar").insert(mapper.ctx());
			Assert.assertEquals(51, new User().countAll(mapper.ctx(), " where age>", ques(50)));
		} finally {
			session.close();
		}
	}

}
