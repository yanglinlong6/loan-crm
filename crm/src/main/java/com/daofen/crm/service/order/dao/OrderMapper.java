package com.daofen.crm.service.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.OrderPO;


@Component
@Mapper
public interface OrderMapper {
	
	List<OrderPO> getOrderList(PageVO<OrderPO> vo);
	
	void addOrderPO(OrderPO po);
	
	void updateOrderPO(OrderPO po);
	
	Integer getOrderListCount(PageVO<OrderPO> vo);
	
}
