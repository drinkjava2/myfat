package com.github.drinkjava2.demo;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.gctx;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.iQueryForLongValue;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Assert;
import org.junit.Test;

import com.github.drinkjava2.jsqlbox.SqlBoxContext;
import com.github.drinkjava2.myfat.Pagin;
import com.zaxxer.hikari.HikariDataSource;

public class TestDemo {
	public static interface UserMapper {
		@Select("select concat(firstName, ' ', lastName) as USERNAME, age as AGE from users where age>#{age}")
		List<Map<String, Object>> getOlderThan(int age);
	}

	private SqlSessionFactory ConfigSqlSessionFactory() {
		HikariDataSource dataSource = new HikariDataSource();// DataSource

		// H2 is a memory database
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setJdbcUrl("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0");
		dataSource.setUsername("sa");
		dataSource.setPassword("");

		SqlBoxContext ctx = new SqlBoxContext(dataSource);
		SqlBoxContext.setGlobalSqlBoxContext(ctx);
		SqlBoxContext.setGlobalNextAllowShowSql(true);
		String[] ddlArray = ctx.toDropAndCreateDDL(User.class);
		for (String ddl : ddlArray)
			ctx.quiteExecute(ddl);
		for (int i = 1; i <= 100; i++)
			new User().putField("firstName", "Foo" + i, "lastName", "Bar" + i, "age", i).insert();

		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("demo", transactionFactory, dataSource);
		Configuration configuration = new Configuration(environment);
		configuration.addMapper(UserMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		configuration.addInterceptor(new Pagin());
		return sqlSessionFactory;
	}

	@Test
	public void doTest() {
		SqlSession session = null;
		try {
			session = ConfigSqlSessionFactory().openSession();
			Connection conn = session.getConnection();
			Assert.assertEquals(100, iQueryForLongValue(conn,"select count(*) from users"));

			List<Map<String, Object>> users;
			Pagin.set(3, 10, gctx().getDialect());
			users = session.getMapper(UserMapper.class).getOlderThan(50);
			Assert.assertEquals(10, users.size());
			for (Map<String, Object> map : users)
				System.out.println("UserName=" + map.get("USERNAME") + ", age=" + map.get("AGE"));
		} finally {
			session.close();
		}
	}

}
