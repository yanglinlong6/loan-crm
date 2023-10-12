package com.daofen.crm.service.media;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.dao.SourceMapper;
import com.daofen.crm.service.media.model.SourcePO;

@Component
public class ISourceService implements SourceService{
	
	@Autowired
	private SourceMapper sourceMapper;
	
	@Override
	public PageVO<SourcePO> getSourceList(PageVO<SourcePO> vo) {
		vo.setData(sourceMapper.getSourceList(vo));
		vo.setTotalCount(sourceMapper.getSourceListCount(vo));
		return vo;
	}

	@Override
	public void addSource(SourcePO po) {
		sourceMapper.addSource(po);
	}

	@Override
	public void delSource(Long id) {
		sourceMapper.delSource(id);
	}

	@Override
	public void updateSource(SourcePO po) {
		sourceMapper.updateSource(po);
	}

}
