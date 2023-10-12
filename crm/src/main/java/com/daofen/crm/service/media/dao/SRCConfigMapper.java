package com.daofen.crm.service.media.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.media.model.SRCConfigPO;

@Component
@Mapper
public interface SRCConfigMapper {
	
	List<SRCConfigPO> getConfigList(PageVO<SRCConfigPO> vo);
	
	void addSRCConfigPO(SRCConfigPO po);
	
	void updateSRCConfigPO(SRCConfigPO po);

	void delSRCConfigPO(Long id);
	
	Integer getConfigListCount(PageVO<SRCConfigPO> vo);
}
