package com.daofen.crm.service.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.UncontactedVO;

@Component
@Mapper
public interface CustomerMapper {
	
	List<CustomerPO> getCustomerList(PageVO<CustomerPO> vo);
	
	List<CustomerPO> getCustomerList1(PageVO<CustomerPO> vo);
	
	List<CustomerPO> getCustomerList2(PageVO<CustomerPO> vo);
	
	void addCustomer(CustomerPO po);
	
	void updateCustomer(CustomerPO po);
	
	void delCustomer(Long id);
	
	Integer getCustomerListCount(PageVO<CustomerPO> vo);
	
	Integer getCustomerListCount1(PageVO<CustomerPO> vo);
	
	Integer getCustomerListCount2(PageVO<CustomerPO> vo);
	
	List<UncontactedVO> getUncontactedUsersNum(Long id);
	
	List<UncontactedVO> getUncontactedTeamUsersNum(JSONObject params);
	
	List<JSONObject> selMediaReport(JSONObject params);
	
	List<JSONObject> selCustReport(JSONObject params);
	
	List<JSONObject> getMatrix();
	
	List<JSONObject> getMatrixAssets();
	
	List<JSONObject> getMatrixBusiness();
	
	List<JSONObject> getMatrixOffice();
	
	CustomerPO selSocketCust(String user);
	
	List<JSONObject> getMatrixNew();
}
