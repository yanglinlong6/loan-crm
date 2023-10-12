package com.daofen.crm.service.order;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.OperationLogPO;

public interface OperationLogService {
	
	PageVO<OperationLogPO> getOperationLogList(PageVO<OperationLogPO> vo);
	
	void addOperationLog(OperationLogPO po);
	
	void delOperationLog(Long id);
	
}
