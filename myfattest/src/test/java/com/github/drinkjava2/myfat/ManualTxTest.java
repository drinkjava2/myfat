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

	HikariDataSource ds;

	@Before
	public void init() {
		SqlBoxContext.setGlobalNextAllowShowSql(true);
		ds = new HikariDataSource();// DataSource
		ds.setDriverClassName("org.h2.Driver"); // H2 Memory database
		ds.setJdbcUrl("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		ds.setUsername("sa");
		ds.setPassword("");
		SqlBoxContext.setGlobalSqlBoxContext(new SqlBoxContext(ds));// 全局jSqlBox上下文
		gctx().setName("GCTX");
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

		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();

			// MyBatis的Session有了增强的SQL方法，jSqlBox中的pinte系列方法可以用
			Assert.assertEquals(50, session.iQueryForLongValue("select count(*) from user_tb where age>", ques(50),
					" and age <= ", ques(200)));

			UserMapper mapper = session.getMapper(UserMapper.class);
			
			mapper.ctx().setAllowShowSQL(true);
			mapper.ctx().eCountAll(User.class, " where 1<>0");// TODO: debug log did not show
			System.out.println(mapper.ctx().getName());
			System.out.println(gctx().getName());

			// mapper也有增强的SQL方法了，pintea系列方法都可以用
			Assert.assertEquals(100, mapper.eCountAll(User.class, " where age>?", param(0), " and age<", ques(200)));

			// ActiveRecord写法，基于当前session的源据源
			Assert.assertEquals(100, new User().countAll(mapper.ctx()));

			// 基于jSqlBox上下文的ActiveRecord，和MyBatis没有半毛钱关系
			Assert.assertEquals(100, new User().countAll());

			// 无侵入的分页
			Helper.pagin(2, 10);
			try {
				List<User> users = mapper.getOlderThan(50);
				Assert.assertEquals(10, users.size());
			} finally {
				Helper.clear();
			}

			Assert.assertEquals(100, new User().countAll());

			// 插入一行
			new User().putField("firstName", "Tom").insert(mapper.ctx());
			new Tail().putTail("FIRST_NAME", "Sam").insert(mapper.ctx(), tail("user_tb"));
			Assert.assertEquals(102, new User().countAll(mapper.ctx()));
			System.out.println(1 / 0);// 测试事务回滚
		} finally {
			session.close();
		}
		Assert.assertEquals(100, new User().countAll());
	}

}
