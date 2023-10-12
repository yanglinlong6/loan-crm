package com.daofen.crm.service.customer;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.dao.MediaCustomerBO;
import com.daofen.crm.service.customer.model.CallLogPO;
import com.daofen.crm.service.customer.model.CirculationLogPO;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.RemarkPO;
import com.daofen.crm.service.customer.model.UncontactedVO;

public interface CustomerFacade {
	
	PageVO<CustomerPO> getMyCustomerList(PageVO<CustomerPO> vo);
	
	void addCustomer(CustomerPO po);
	
	void joinPublicPool(Long customerId);
	
	void processCustomer(CustomerPO po);
	
	PageVO<RemarkPO> getCustomerRemarks(PageVO<RemarkPO> vo);
	
	PageVO<CirculationLogPO> getCustomerCirculationLog(PageVO<CirculationLogPO> vo);
	
	List<UncontactedVO> getUncontactedUsersNum(Long id);
	
	PageVO<CustomerPO> getCustomerList(PageVO<CustomerPO> vo);
	
	PageVO<CustomerPO> getTeamCustomerList(PageVO<CustomerPO> vo);
	
	PageVO<CustomerPO> getCirculationCustomerList(PageVO<CustomerPO> vo);
	
	PageVO<CustomerPO> getWaitCustomerList(PageVO<CustomerPO> vo);
	
	PageVO<CustomerPO> getPublicPoolList(PageVO<CustomerPO> vo);
	
	List<UncontactedVO> getUncontactedTeamUsersNum(Long teamId);
	
	void addRemark(RemarkPO po);
	
	void delRemark(Long id);
	
	void addCallLog(CallLogPO po);
	
	void distributionAgainCust(CustomerPO po);
	
	void distributionCust(CustomerPO po);
	
	void joinMyCustomer(CustomerPO po);
	
	List<JSONObject> selMediaReport(JSONObject params);
	
	List<JSONObject> selCustReport(JSONObject params);
	
	void addMeidaCustomer(MediaCustomerBO bo);
	
	double getMatrix(JSONObject o);
	
	void replaceMatrix();
	
	boolean checkMobile(MediaCustomerBO bo);
}
