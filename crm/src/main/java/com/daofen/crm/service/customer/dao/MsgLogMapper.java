package com.daofen.crm.service.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.MsgLogPO;

@Component
@Mapper
public interface MsgLogMapper {
	
	List<MsgLogPO> getMsgLogList(PageVO<MsgLogPO> vo);
	
	Integer getMsgLogCount(PageVO<MsgLogPO> vo);
	
	void addMsgLog(MsgLogPO po);
	
	void delMsgLog(Long id);
	
}
