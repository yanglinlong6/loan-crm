package com.help.loan.distribute.service.cityAptitude.dao;


import com.help.loan.distribute.service.cityAptitude.model.CityAptitudeRatePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CityAptitudeRatePOMapper {

    List<CityAptitudeRatePO> selectCurrentCityAptitudeRate(@Param("dateString") String dateString, @Param("startDate")String startDate, @Param("endDate")String endDate);

    CityAptitudeRatePO selectCityAptitudeRatePO(@Param("dateString")String dateString,@Param("city")String city);

    int insertCityAptitudeRate(CityAptitudeRatePO record);

    int updateCityAptitudeRate(CityAptitudeRatePO record);

    /**
     * 查询机构城市无可贷点占比，保留两位小数
     * @param orgId 机构id
     * @param city 城市
     * @param startDate 开始日期：2020-04-20 00:00:00
     * @param endDate 结束日期 2020-04-20 23:59:59
     * @return 机构城市无可贷点占比
     *
     */
    Map<String,Object> selectOrgCityNoAptitudeRate(@Param("orgId")Long orgId, @Param("city")String city, @Param("startDate")String startDate, @Param("endDate")String endDate);

    /**
     * 查询机构城市各项资质占比
     * @param orgId 机构id
     * @param city 城市
     * @param startDate 开始日期：2020-04-20 00:00:00
     * @param endDate 结束日期 2020-04-20 23:59:59
     * @return CityAptitudeRatePO
     */
    CityAptitudeRatePO selectOrgCityAptitudeRate(@Param("orgId")Long orgId, @Param("city")String city, @Param("startDate")String startDate, @Param("endDate")String endDate);
}