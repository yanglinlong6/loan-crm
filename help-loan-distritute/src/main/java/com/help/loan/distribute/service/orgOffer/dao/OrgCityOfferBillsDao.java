package com.help.loan.distribute.service.orgOffer.dao;


import com.help.loan.distribute.service.orgOffer.model.OrgCityOfferBillsPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface OrgCityOfferBillsDao{

    /**
     * 初始化当日机构城市结算数据
     * @param dateNum 日期字符串：20200915
     * @param startDate 2020-09-15 00:00:00
     * @param endDate  2020-09-15 23:59:59
     * @return
     */
    int insertBySelect(@Param("dateNum") String dateNum,@Param("startDate") String startDate, @Param("endDate")String endDate);

    List<OrgCityOfferBillsPO> selectAllByDateNum(@Param("dateNum") String dateNum,@Param("startDate") String startDate, @Param("endDate")String endDate);

    void deleteAllByDateNum(@Param("dateNum") String dateNum);

    List<OrgCityOfferBillsPO> selectAllByOrgIdAndDateNum(@Param("orgId")Long orgId,@Param("dateNum") String dateNum);

    List<OrgCityOfferBillsPO> selectAllByOrgIdAndDate(@Param("orgId")Long orgId,@Param("startDate") String startDate,@Param("endDate")String endDate);

}