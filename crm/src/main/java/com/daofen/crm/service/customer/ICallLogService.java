package com.daofen.crm.service.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.dao.CallLogMapper;
import com.daofen.crm.service.customer.model.CallLogPO;

@Component
public class ICallLogService implements CallLogService{
	
	@Autowired
	private CallLogMapper callLogMapper;
	
	@Override
	public PageVO<CallLogPO> getCallLogList(PageVO<CallLogPO> vo) {
		vo.setData(callLogMapper.getCallLogList(vo));
		vo.setTotalCount(callLogMapper.getCallLogListCount(vo));
		return vo;
	}

	@Override
	public void addCallLog(CallLogPO po) {
		callLogMapper.addCallLog(po);
	}

	@Override
	public void delCallLog(Long id) {
		callLogMapper.delCallLog(id);
	}

}
