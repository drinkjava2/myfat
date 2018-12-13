package com.github.drinkjava2.myfat.entity;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.myfat.RootMapper;

public interface UserMapper extends RootMapper<User> {
	@Select("select * from user_tb where age>#{age}")
	List<User> getOlderThan(int age);
}