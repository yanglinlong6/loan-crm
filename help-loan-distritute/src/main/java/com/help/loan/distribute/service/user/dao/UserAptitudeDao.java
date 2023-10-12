package com.help.loan.distribute.service.user.dao;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserNotAptitudeSendStrategyRecPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface UserAptitudeDao {

    UserAptitudePO get(String userId);

    List<UserAptitudePO> getByLevel(Integer level);




    /**
     *
     * @param level
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserAptitudePO> getByLevelByLimitDate(@Param("level") String level,@Param("startDate")String startDate,@Param("endDate")String endDate);

    @Transactional
    void update(UserAptitudePO po);

    @Transactional
    void create(UserAptitudePO po);

    /**
     * 获取给定日期范围内的
     * @param startDate 开始日期
     * @param endDate 结束日子
     * @return
     */
    List<Map<String,Object>> getTodayAllCostomer(@Param("startDate") String startDate, @Param("endDate") String endDate);


    /**
     * 当天机构城市贷款金额已分发成功数量
     *
     * @param orgId      机构id
     * @param city       城市
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param loanAmount 贷款金额字符，例如：《3-5万》
     * @return
     */
    Integer todayOrgCityLoanAmountCount(@Param("orgId") Long orgId, @Param("city") String city, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("loanAmount") String loanAmount);

    /**
     * 当天机构贷款金额已分发成功数量 [注意：该方法没有经过测试]
     *
     * @param orgId      机构id
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param loanAmount 贷款金额字符，例如：《3-5万》
     * @return 已分发的数量
     */
    Integer todayOrgLoanAmountCount(@Param("orgId") Long orgId, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("loanAmount") String loanAmount);

    /**
     * name字段是空的占机构已分发成功数量
     *
     * @param orgId     机构id
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return name字段是空的占机构已分发成功数量
     */
    Integer todayOrgEmptyNameCount(@Param("orgId") Long orgId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询没有可贷点用户
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return List<UserAptitudePO>
     */
    List<UserAptitudePO> selectNotAptitudeUser(@Param("startDate") String startDate,@Param("endDate")String endDate);


    void insertUserNotAptitudeSendStrategyRec(UserNotAptitudeSendStrategyRecPO po);
    
    JSONObject selectCount(JSONObject o);

}
