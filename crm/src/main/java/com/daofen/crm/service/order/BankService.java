package com.daofen.crm.service.order;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.BankPO;


public interface BankService {
	
	PageVO<BankPO> getBankList(PageVO<BankPO> vo);
	
	void addBank(BankPO po);
	
	void delBank(Long id);
	
}
