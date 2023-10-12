package com.daofen.crm.service.customer.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.service.customer.model.MemorandumPO;

@Component
@Mapper
public interface MemorandumMapper {
	
	MemorandumPO get(Long custId);
	
	void add(MemorandumPO po);
	
	void del(Long id);
	
	void update(MemorandumPO po);
	
	List<MemorandumPO> getList();
	
}
