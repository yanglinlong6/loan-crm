package com.daofen.crm.service.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.CallLogPO;

@Component
@Mapper
public interface CallLogMapper {
	
	List<CallLogPO> getCallLogList(PageVO<CallLogPO> vo);
	
	void addCallLog(CallLogPO po);
	
	void delCallLog(Long id);
	
	Integer getCallLogListCount(PageVO<CallLogPO> vo);
	
}
