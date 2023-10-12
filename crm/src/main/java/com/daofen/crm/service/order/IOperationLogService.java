package com.daofen.crm.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.dao.OperationLogMapper;
import com.daofen.crm.service.order.model.OperationLogPO;

@Component
public class IOperationLogService implements OperationLogService{
	
	@Autowired
	private OperationLogMapper operationLogMapper;
	
	@Override
	public PageVO<OperationLogPO> getOperationLogList(PageVO<OperationLogPO> vo) {
		vo.setData(operationLogMapper.getOperationLogList(vo));
		vo.setTotalCount(operationLogMapper.getOperationLogListCount(vo));
		return vo;
	}

	@Override
	public void addOperationLog(OperationLogPO po) {
		operationLogMapper.addOperationLog(po);
	}

	@Override
	public void delOperationLog(Long id) {
		operationLogMapper.delOperationLog(id);
	}

}
