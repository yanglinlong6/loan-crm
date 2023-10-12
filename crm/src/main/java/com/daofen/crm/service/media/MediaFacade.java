package com.daofen.crm.service.media;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.MediaPO;
import com.daofen.crm.service.media.model.SRCConfigPO;
import com.daofen.crm.service.media.model.SourcePO;

public interface MediaFacade {
	
	PageVO<MediaPO> getMediaList(PageVO<MediaPO> vo);
	
	void addMedia(MediaPO po);
	
	void updateMedia(MediaPO po);
	
	void delMedia(Long id);
	
	PageVO<SourcePO> getSourceList(PageVO<SourcePO> vo);
	
	void addSource(SourcePO po);
	
	void updateSource(SourcePO po);
	
	void delSource(Long id);
	
	PageVO<SRCConfigPO> getConfigList(PageVO<SRCConfigPO> vo);
	
	void addConfig(SRCConfigPO po);
	
	void updateConfig(SRCConfigPO po);
	
	void delConfig(Long id);
	
}
