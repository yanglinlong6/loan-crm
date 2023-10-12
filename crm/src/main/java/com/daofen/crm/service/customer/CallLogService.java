package com.daofen.crm.service.customer;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.CallLogPO;

public interface CallLogService {
	
	PageVO<CallLogPO> getCallLogList(PageVO<CallLogPO> vo);
	
	void addCallLog(CallLogPO po);
	
	void delCallLog(Long id);
	
}
