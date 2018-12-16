package com.example.jsqlboxinspringboot;

import static com.github.drinkjava2.jdbpro.JDBPRO.param;
import static com.github.drinkjava2.jsqlbox.JSQLBOX.*;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.jsqlboxinspringboot.entity.Customer;
import com.example.jsqlboxinspringboot.entity.CustomerMapper;

@Service
public class TestService {
	@Autowired
	private CustomerMapper customerMapper;

	@Transactional
	public void insertTwoCustomer() {
		new Customer().putField("name", "a").insert();
		customerMapper.insertByIdAndName("1", "b");
	}

	@Transactional
	public void deleteOneCustomer() {
		new Customer().putField("id", "1").delete(" and name=?", param("b"));
	}

	@Transactional
	public void insertWithDiv0() {
		for (int i = 0; i < 100; i++)
			new Customer().putField("name", "Foo" + i).insert();
		Assert.assertEquals(15, new Customer().findAll(" where name<>''", pagin(2, 15)).size());

		customerMapper.insertByIdAndName("2", "Sam");
		customerMapper.iExecute("insert into customer (id,name) values (?,?)", param("3", "Tom"));
		iExecute("insert into customer (id,name) values (?,?)", param("4", "Cat"));
		new Customer().insert();
		Assert.assertEquals(104, new Customer().countAll());
		System.out.println(1 / 0);
	}
}