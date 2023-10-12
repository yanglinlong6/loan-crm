package com.daofen.admin.service.city.dao;

import com.daofen.admin.service.city.model.CityPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 城市模块数据库映射类
 */
@Component
@Mapper
public interface CityDao {

    /**
     * 查询所有城市信息
     *
     * @param level 城市等级：级别(1省份,2城市,3区县) ；如果level=3，则表示获取三级：省份，城市，区(县)
     * @return List<CityPO>
     */
    List<CityPO> selectAll(@Param("level") Byte level);

    CityPO selectCity(@Param("city")String city);

    /**
     * 查询所有投放城市信息
     *
     * @param level 城市等级：级别(1省份,2城市,3区县) ；如果level=3，则表示获取三级：省份，城市，区(县)
     * @return List<CityPO>
     */
    List<CityPO> selectAllMp(@Param("level") Byte level);

}