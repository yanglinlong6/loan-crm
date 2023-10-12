package com.daofen.crm.service.media;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.MediaPO;

public interface MediaService {
	
	PageVO<MediaPO> getCPMediaList(PageVO<MediaPO> vo);
	
	void addCPMedia(MediaPO po);
	
	void updateCPMedia(MediaPO po);
	
	void delCPMedia(Long id);
	
}
