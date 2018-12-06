package com.github.drinkjava2.myfat;

import static com.github.drinkjava2.jdbpro.JDBPRO.*;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.tail;

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

import com.github.drinkjava2.jdialects.annotation.jpa.Table;
import com.github.drinkjava2.jsqlbox.ActiveEntity;
import com.github.drinkjava2.jsqlbox.SqlBoxContext;
import com.github.drinkjava2.jsqlbox.Tail;
import com.zaxxer.hikari.HikariDataSource;

public class TestDemo {

	//@formatter:off
	@Table(name="users")
	public static class DemoUser implements ActiveEntity<DemoUser> {
		String firstName, lastName; Integer age;
		public String getFirstName() { return firstName; }
		public void setFirstName(String firstName) { this.firstName = firstName; }
		public String getLastName() { return lastName;}
		public void setLastName(String lastName) {this.lastName = lastName;}
		public Integer getAge() {return age;}
		public void setAge(Integer age) {this.age = age;}
	} //@formatter:on

	public static interface UserMapper extends RootMapper<DemoUser> {
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
		configuration.addInterceptor(new PaginInterceptor());
		return sqlSessionFactory;
	}

	@Test
	public void doTest() {
		SqlSession session = null;
		try {
			session = ConfigSqlSessionFactory().openSession();

			// MyBatis的Session有了增强的SQL方法，jSqlBox中的pinte系列方法可以用
			Assert.assertEquals(50, session.iQueryForLongValue("select count(*) from users where age>", ques(50),
					" and age <= ", ques(100)));
			List<Map<String, Object>> users;
			UserMapper mapper = session.getMapper(UserMapper.class);

			// 对任意MyBatis查询都可以分页了，无侵入性
			session.page(2, 10); // 也可以写成mapper.page(2,10)
			try {
				users = mapper.getOlderThan(50);
				Assert.assertEquals(10, users.size());
			} finally {
				session.noPage(); // 消除分页设定，也可以写成mapper.noPage()
			}

			// mapper也有增强的SQL方法了，pintea系列方法都可以用
			Assert.assertEquals(50, mapper.eCountAll(DemoUser.class, " where age>?", param(50)));
			Assert.assertEquals(50, mapper.countAll(" where age>", ques(50)));

			// Tail也出现在mapper中了
			Assert.assertEquals(100, mapper.eCountAll(Tail.class, tail("users")));

			// ActiveRecord写法当然有，但和MyBatis没有一毛钱关系了
			Assert.assertEquals(50, new DemoUser().countAll(" where age>50"));

			for (Map<String, Object> map : users)
				System.out.println("UserName=" + map.get("USERNAME") + ", age=" + map.get("AGE"));

		} finally {
			session.close();
		}
	}

}
