package com.example.jsqlboxinspringboot;

import static com.github.drinkjava2.jsqlbox.JSQLBOX.iExecute;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.jsqlboxinspringboot.entity.Customer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JsqlboxInSpringbootApplicationTests {
	@Autowired
	TestService service;

	@Test
	public void testInsert() {
		iExecute("delete from customer");
		Assert.assertEquals(0, new Customer().countAll());

		service.insertTwoCustomer();
		Assert.assertEquals(2, new Customer().countAll());

		service.deleteOneCustomer();
		Assert.assertEquals(1, new Customer().countAll());
		System.out.println("==testInsert done!===");
	}

	@Test
	public void testTransaction() {
		iExecute("delete from customer");
		try {
			service.insertWithDiv0();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Assert.assertEquals(0, new Customer().countAll());
		System.out.println("==testTransaction done!===");
	}

}
