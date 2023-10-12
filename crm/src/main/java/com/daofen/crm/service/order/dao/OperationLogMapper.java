package com.daofen.crm.service.order.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.model.OperationLogPO;


@Component
@Mapper
public interface OperationLogMapper {
	
	List<OperationLogPO> getOperationLogList(PageVO<OperationLogPO> vo);
	
	void addOperationLog(OperationLogPO po);
	
	void delOperationLog(Long id);
	
	Integer getOperationLogListCount(PageVO<OperationLogPO> vo);
	
}
