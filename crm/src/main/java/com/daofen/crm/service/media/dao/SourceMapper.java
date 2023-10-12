package com.daofen.crm.service.media.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.SourcePO;

@Component
@Mapper
public interface SourceMapper {
	
	List<SourcePO> getSourceList(PageVO<SourcePO> vo);
	
	void addSource(SourcePO po);
	
	void delSource(Long id);
	
	void updateSource(SourcePO po);
	
	Integer getSourceListCount(PageVO<SourcePO> vo);
	
}
