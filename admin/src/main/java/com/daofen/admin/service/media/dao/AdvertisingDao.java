package com.daofen.admin.service.media.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.daofen.admin.service.media.model.AdvertisingPO;

@Mapper
public interface AdvertisingDao {
	
	void add(List<AdvertisingPO> list);
	
}
