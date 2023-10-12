package com.loan.cps.dao;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.entity.UserAptitudePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserAptitudeDao {
	
	UserAptitudePO get(String userId);
	
	List<UserAptitudePO> getByLevel(Integer level);
	
	void update(UserAptitudePO po);
	
	void create(UserAptitudePO po);
	
	void add(UserAptitudePO po);
	
	UserAptitudePO getByMobile(String mobile);

	UserAptitudePO getByMobileMD5(String mobileMD5);

	UserAptitudePO getByMobileByCreateBy(@Param("mobile") String mobile, @Param("createBy") String createBy);
	
	List<JSONObject> selCityChannel(UserAptitudePO po);
	
}
