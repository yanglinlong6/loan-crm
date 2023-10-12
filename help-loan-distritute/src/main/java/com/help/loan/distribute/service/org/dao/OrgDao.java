package com.help.loan.distribute.service.org.dao;

import com.help.loan.distribute.service.org.model.OrgAptitudePO;
import com.help.loan.distribute.service.org.model.OrgDistributeStatisticsBO;
import com.help.loan.distribute.service.org.model.OrgPO;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface OrgDao {

    /**
     * 新增机构数据
     *
     * @param record
     * @return
     */
    int insertOrg(OrgPO record);

    /**
     * 根据机构id查询机构机构记录
     *
     * @param orgId 机构id
     * @return OrgPO
     */
    OrgPO selectOrg(@Param("orgId") Long orgId);

    /**
     * 查询所有机构列表
     *
     * @return List<OrgPO>
     */
    List<OrgPO> selectAllOrg();

    /**
     * 更新机构信息
     *
     * @param org OrgPO
     * @return
     */
    int updateOrg(OrgPO org);

    /**
     * 新增机构限制资质记录
     *
     * @param orgAptitude OrgAptitudePO
     * @return
     */
    int insertOrgAptitude(OrgAptitudePO orgAptitude);

    /**
     * 更新修改机构限制资质信息
     *
     * @param orgAptitude OrgAptitudePO
     * @return
     */
    int updateOrgAptitude(OrgAptitudePO orgAptitude);

    /**
     * 查询机构限制条件
     *
     * @param orgId 机构id
     * @return OrgAptitudePO
     */
    List<OrgAptitudePO> selectOrgAptitude(@Param("orgId") Long orgId);

    List<OrgAptitudePO> selectOrgAptitudeByType(@Param("type")String type);

    /**
     *
     * @param week 星期几
     * @param excludeOrgIds 多个机构id以,号分隔
     * @return List<String> 返回当前主要的分发城市
     */
    List<String> getOrgCityList(@Param("week")String week,@Param("excludeOrgIds")String excludeOrgIds);

    /**
     * @param orgId     机构id
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 机构专属公众号当日分发数量
     */
    Integer selectTodayDistributeSuccessCount(@Param("orgId") Long orgId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询机构某个城市的当时分发数量
     *
     * @param orgId     机构id
     * @param city      城市
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 机构城市当日分数量
     */
    Integer selectTodayDistributeSuccessCount2(@Param("orgId") Long orgId,
                                               @Param("city") String city,
                                               @Param("startDate") String startDate,
                                               @Param("endDate") String endDate);

    Integer selectTodayDistributeRepeatCount(@Param("orgId") String orgId,
                                             @Param("city") String city,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate);

    /**
     * 有公积金且房在机构城市分发成功中的数量占比
     * @param orgId 机构id
     * @param city 城市
     * @param startDate 开始日期：例如：2020-04-01 00:00:00
     * @param endDate 结束日期 例如：2020-04-01 23:59:59
     * @return 公积金和房在机构城市分发成功中的数量占比
     */
    Integer selectTodayHouseAndPublicFundCountInDistributeSuccess(@Param("orgId") Long orgId,
                                                                 @Param("city") String city,
                                                                 @Param("startDate") String startDate,
                                                                 @Param("endDate") String endDate);


    List<OrgDistributeStatisticsBO> selectOrgDistributeCount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String,Object>> selectOrgDistributeCountSummation(@Param("startDate") String startDate, @Param("endDate") String endDate);

    Integer selectDistributeSuccessNotNameCounts(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("orgId")Long orgId);

    List<Map<String,Object>> selectChannelQualityForAverage(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("mainCityList") String mainCityList);

    List<Map<String,Object>> selectChannelQuality(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("mainCityList") String mainCityList);

    List<Map<String,Object>> selectonlineLenderCountSummation(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 多个机构id，以，号隔开
     * @param orgIds 多个机构id，以，号隔开
     * @return List<UserAptitudePO>
     */
    List<UserAptitudePO> selectOrgDistributeSuccessUser(@Param("startDate")String startDate,@Param("endDate")String endDate,@Param("orgIds")String orgIds);

}