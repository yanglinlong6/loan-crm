package com.daofen.crm.service.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.BankPO;


@Component
@Mapper
public interface BankMapper {
	
	List<BankPO> getBankList(PageVO<BankPO> vo);
	
	void addBank(BankPO po);
	
	void delBank(Long id);
	
	Integer getBankListCount(PageVO<BankPO> vo);
	
}
