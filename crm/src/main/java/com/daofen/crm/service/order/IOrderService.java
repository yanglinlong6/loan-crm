package com.daofen.crm.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.dao.OrderMapper;
import com.daofen.crm.service.order.model.OrderPO;

@Component
public class IOrderService implements OrderService{
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Override
	public PageVO<OrderPO> getOrderList(PageVO<OrderPO> vo) {
		vo.setData(orderMapper.getOrderList(vo));
		vo.setTotalCount(orderMapper.getOrderListCount(vo));
		return vo;
	}

	@Override
	public void addOrderPO(OrderPO po) {
		orderMapper.addOrderPO(po);
	}

	@Override
	public void updateOrderPO(OrderPO po) {
		orderMapper.updateOrderPO(po);
	}

}
