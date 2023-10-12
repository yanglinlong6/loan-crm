package com.daofen.crm.service.customer.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.CirculationLogPO;

@Component
@Mapper
public interface CirculationLogMapper {

	List<CirculationLogPO> getCirculationLogPage(PageVO<CirculationLogPO> vo);

	void addCirculationLog(CirculationLogPO po);
	
	void delCirculationLog(Long id);
	
	Integer getCirculationLogPageCount(PageVO<CirculationLogPO> vo);
}
