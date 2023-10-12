package com.loan.cps.dao;

import org.apache.ibatis.annotations.Mapper;

import com.loan.cps.entity.DispatcheRecPO;

@Mapper
public interface DispatcheRecDao {
	
	void add(DispatcheRecPO po);
	
}
