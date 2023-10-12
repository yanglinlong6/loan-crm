package com.daofen.crm.service.order;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.order.model.BankPO;
import com.daofen.crm.service.order.model.OperationLogPO;
import com.daofen.crm.service.order.model.OrderPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class IOrderFacade implements OrderFacade{
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OperationLogService operationLogService;
	
	@Autowired
	private BankService bankService;

	@Override
	public PageVO<OrderPO> getOrderList(PageVO<OrderPO> vo) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		OrderPO param = vo.getParam();
		if(param == null) {
			param = new OrderPO();
		}
		param.setCompanyId(loginThreadLocal.getCompanyId());
		param.setDataState(0);
		vo.setParam(param);
		return orderService.getOrderList(vo);
	}

	@Override
	public void addOrder(OrderPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setCompanyId(loginThreadLocal.getCompanyId());
		po.setShopId(loginThreadLocal.getShopId());
		po.setTeamId(loginThreadLocal.getTeamId());
		po.setCreateBy(loginThreadLocal.getName());
		po.setCreateDate(new Date());
		orderService.addOrderPO(po);
	}
	
	@Transactional
	@Override
	public void updateOrder(OrderPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setUpdateBy(loginThreadLocal.getName());
		po.setUpdateDate(new Date());
		orderService.updateOrderPO(po);
		OperationLogPO log = new OperationLogPO();
		log.setBusinessId(loginThreadLocal.getId());
		log.setCreateBy(loginThreadLocal.getName());
		log.setCreateDate(new Date());
		log.setType(po.getState());
		log.setOrderId(po.getId());
		operationLogService.addOperationLog(log);
	}

	@Override
	public void delOrder(Long id) {
		OrderPO po = new OrderPO();
		po.setId(id);
		po.setDataState(CrmConstant.Order.DataState.DELETE);
		orderService.updateOrderPO(po);
	}

	@Override
	public PageVO<BankPO> getBankList(PageVO<BankPO> vo) {
		return bankService.getBankList(vo);
	}

	@Override
	public void addBank(BankPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setCreateBy(loginThreadLocal.getName());
		po.setCreateDate(new Date());
		bankService.addBank(po);
	}

}
