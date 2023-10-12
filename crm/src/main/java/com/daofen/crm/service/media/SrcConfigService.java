package com.daofen.crm.service.media;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.SRCConfigPO;

public interface SrcConfigService {
	
	PageVO<SRCConfigPO> getConfigList(PageVO<SRCConfigPO> vo);
	
	void addSRCConfigPO(SRCConfigPO po);
	
	void updateSRCConfigPO(SRCConfigPO po);

	void delSRCConfigPO(Long id);
	
}
