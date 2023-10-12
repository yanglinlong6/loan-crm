package com.daofen.crm.service.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.dao.MediaMapper;
import com.daofen.crm.service.media.model.MediaPO;

@Component
public class IMediaService implements MediaService{
	
	@Autowired
	private MediaMapper mediaMapper;

	@Override
	public PageVO<MediaPO> getCPMediaList(PageVO<MediaPO> vo) {
		vo.setData(mediaMapper.getCPMediaListPage(vo));
		vo.setTotalCount(mediaMapper.getCPMediaListPageCount(vo));
		return vo;
	}

	@Override
	public void addCPMedia(MediaPO po) {
		mediaMapper.addCPMedia(po);
	}

	@Override
	public void updateCPMedia(MediaPO po) {
		mediaMapper.updateCPMedia(po);
	}

	@Override
	public void delCPMedia(Long id) {
		mediaMapper.delCPMedia(id);
	}

}
