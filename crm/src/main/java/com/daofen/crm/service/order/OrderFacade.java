package com.daofen.crm.service.order;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.BankPO;
import com.daofen.crm.service.order.model.OrderPO;

public interface OrderFacade {
	
	PageVO<OrderPO> getOrderList(PageVO<OrderPO> vo);
	
	void addOrder(OrderPO po);
	
	void updateOrder(OrderPO po);
	
	void delOrder(Long id);
	
	PageVO<BankPO> getBankList(PageVO<BankPO> vo);
	
	void addBank(BankPO po);
	
}
