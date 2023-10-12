package com.daofen.crm.service.media;

import java.util.List;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.SourcePO;

public interface SourceService {
	
	PageVO<SourcePO> getSourceList(PageVO<SourcePO> vo);
	
	void addSource(SourcePO po);
	
	void delSource(Long id);
	
	void updateSource(SourcePO po);
	
}
