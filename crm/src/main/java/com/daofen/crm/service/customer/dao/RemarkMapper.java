package com.daofen.crm.service.customer.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.model.RemarkPO;

@Component
@Mapper
public interface RemarkMapper {
	
	List<RemarkPO> getRemarkList(PageVO<RemarkPO> vo);
	
	void addRemark(RemarkPO po);
	
	void delRemark(Long id);
	
	Integer getRemarkListCount(PageVO<RemarkPO> vo);
	
}
