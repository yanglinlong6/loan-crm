package com.daofen.crm.service.media.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.MediaPO;

@Component
@Mapper
public interface MediaMapper {

	List<MediaPO> getCPMediaListPage(PageVO<MediaPO> vo);

	void addCPMedia(MediaPO po);
	
	void updateCPMedia(MediaPO po);
	
	void delCPMedia(Long id);
	
	Integer getCPMediaListPageCount(PageVO<MediaPO> vo);
}
