package com.daofen.crm.service.customer;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.RemarkPO;

public interface RemarkService {
	
	PageVO<RemarkPO> getRemarkList(PageVO<RemarkPO> vo);
	
	void addRemark(RemarkPO po);
	
	void delRemark(Long id);
	
}
