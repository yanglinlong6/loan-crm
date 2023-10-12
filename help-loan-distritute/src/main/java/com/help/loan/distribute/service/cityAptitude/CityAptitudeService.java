package com.help.loan.distribute.service.cityAptitude;

import com.help.loan.distribute.service.cityAptitude.model.CityAptitudeRatePO;

public interface CityAptitudeService {


    void updateCityAptitudeRate(String dateString);


    /**
     * 获取一段日期内机构城市各项资质占比
     * @param orgId 机构id
     * @param city 城市
     * @param startDate 开始日期：2020-04-20 00:00:00
     * @param endDate 结束日期 2020-04-20 23:59:59
     * @return 机构城市无可贷点占比
     */
    CityAptitudeRatePO getCityAptitudeRate(Long orgId,String city,String startDate,String endDate);
}
