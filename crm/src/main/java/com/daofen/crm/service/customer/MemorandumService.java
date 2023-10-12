package com.daofen.crm.service.customer;

import java.util.List;

import com.daofen.crm.service.customer.model.MemorandumPO;

public interface MemorandumService {
	
	MemorandumPO get(Long custId);
	
	void add(MemorandumPO po);
	
	void del(Long id);
	
	void update(MemorandumPO po);
	
	List<MemorandumPO> getList();
	
}
