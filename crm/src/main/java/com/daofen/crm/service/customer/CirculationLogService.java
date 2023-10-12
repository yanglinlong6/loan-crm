package com.daofen.crm.service.customer;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.CirculationLogPO;

public interface CirculationLogService {
	
	PageVO<CirculationLogPO> getCirculationLogPage(PageVO<CirculationLogPO> vo);

	void addCirculationLog(CirculationLogPO po);
	
	void delCirculationLog(Long id);
	
}
