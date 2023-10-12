package com.daofen.crm.service.media;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.dao.SRCConfigMapper;
import com.daofen.crm.service.media.model.SRCConfigPO;

@Component
public class ISrcConfigService implements SrcConfigService{

	@Autowired
	private SRCConfigMapper configMapper;
	
	@Override
	public PageVO<SRCConfigPO> getConfigList(PageVO<SRCConfigPO> vo) {
		vo.setData(configMapper.getConfigList(vo));
		vo.setTotalCount(configMapper.getConfigListCount(vo));
		return vo;
	}

	@Override
	public void addSRCConfigPO(SRCConfigPO po) {
		configMapper.addSRCConfigPO(po);
	}

	@Override
	public void updateSRCConfigPO(SRCConfigPO po) {
		configMapper.updateSRCConfigPO(po);
	}

	@Override
	public void delSRCConfigPO(Long id) {
		configMapper.delSRCConfigPO(id);
	}

}
