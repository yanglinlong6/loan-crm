package com.daofen.crm.service.customer;


import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.UncontactedVO;

public interface CustomerService {
	
	PageVO<CustomerPO> getCustomerList(PageVO<CustomerPO> vo);
	
	void addCustomer(CustomerPO po);
	
	void updateCustomer(CustomerPO po);
	
	void delCustomer(Long id);
	
	List<UncontactedVO> getUncontactedUsersNum(Long id);
	
	List<UncontactedVO> getUncontactedTeamUsersNum(JSONObject param);
	
	List<JSONObject> selMediaReport(JSONObject params);
	
	List<JSONObject> selCustReport(JSONObject params);
	
	List<Double> getMatrix(List<JSONObject> matrix);
	
	double getUserWeightScore(JSONObject o);
	
	void replaceMatrix();
	
	List<CustomerPO> getCustomer(PageVO<CustomerPO> vo);
}
