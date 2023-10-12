package com.daofen.crm.service.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.dao.CirculationLogMapper;
import com.daofen.crm.service.customer.model.CirculationLogPO;

@Component
public class ICirculationLogService implements CirculationLogService{

	@Autowired
	private CirculationLogMapper circulationLogMapper;
	
	@Override
	public PageVO<CirculationLogPO> getCirculationLogPage(PageVO<CirculationLogPO> vo) {
		vo.setData(circulationLogMapper.getCirculationLogPage(vo));
		vo.setTotalCount(circulationLogMapper.getCirculationLogPageCount(vo));
		return vo;
	}

	@Override
	public void addCirculationLog(CirculationLogPO po) {
		circulationLogMapper.addCirculationLog(po);
	}

	@Override
	public void delCirculationLog(Long id) {
		circulationLogMapper.delCirculationLog(id);
	}

}
