package com.example.jsqlboxinspringboot.entity;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.myfat.RootMapper;

/**
 * User映射类 Created by Administrator on 2017/11/24.
 */
@Mapper
public interface CustomerMapper extends RootMapper<Customer> {

	@Select("SELECT * FROM Customer")
	List<Customer> findAllCustomers();

	@Insert("INSERT INTO Customer(id, name) VALUES(#{id}, #{name})")
	int insertByIdAndName(@Param("id") String id, @Param("name") String name);

}