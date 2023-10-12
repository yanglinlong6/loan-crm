package com.daofen.crm.service.order;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.OrderPO;

public interface OrderService {

	PageVO<OrderPO> getOrderList(PageVO<OrderPO> vo);
	
	void addOrderPO(OrderPO po);
	
	void updateOrderPO(OrderPO po);
	
}
