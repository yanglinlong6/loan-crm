package com.loan.cps.dao;

import com.loan.cps.service.city.model.CityPO;
import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityDao {
	
	JSONObject getByName(String name,Integer level);


	/**
	 * 查询所有城市信息
	 *
	 * @param level 城市等级：级别(1省份,2城市,3区县) ；如果level=3，则表示获取三级：省份，城市，区(县)
	 * @return List<CityPO>
	 */
	List<CityPO> selectAll(@Param("level") Byte level);
	
}
