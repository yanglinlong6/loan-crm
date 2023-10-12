package com.help.loan.distribute.service.org;

import com.help.loan.distribute.service.org.model.OrgBO;
import com.help.loan.distribute.service.org.model.OrgDistributeStatisticsBO;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 机构service
 */
public interface OrgService {


    /**
     * 获取所有机构专属机构列表 或者  非专属公众号机构列表
     *
     * @param isExclusive 是否专属公众号机构：true-是专属公众号机构，false-非专属公众号机构
     * @return List<OrgBO>
     */
    List<OrgBO> getAll(Boolean isExclusive);

    /**
     * 获取缓存机构业务对象列表
     *
     * @return List<OrgBO>
     */
    List<OrgBO> getCacheAll(Boolean isExclusive);

    /**
     * 根据机构id获取单个机构业务对象-OrgBO
     *
     * @param orgId 机构id
     * @return OrgBO
     */
    OrgBO getOrgBO(Long orgId);


    public Long chooseExclusiveOrg(UserAptitudePO userAptitude, UserDTO select);

    /**
     * 选择分发机构
     *
     * @param userAptitude UserAptitudePO 客户资质对象
     * @return OrgBO 机构业务对象
     */
    List<OrgBO> chooseOrg(UserAptitudePO userAptitude, Integer chooseCount);

    boolean isMainCity(UserAptitudePO userAptitude);

    /**
     * 再次选择分发机构
     * @param userAptitude UserAptitudePO 客户资质对象
     * @return OrgBO 机构业务对象
     */
//    OrgBO againChooseOrg(UserAptitudePO userAptitude);

    /**
     * 更新机构当天分发成功的数量
     *
     * @param orgBO OrgBO
     */
    @Transactional
    void updateOrgDistributeCount(OrgBO orgBO, UserAptitudePO userAptitude, Boolean isExclusive);

    String[] getOrgCityList();


    /**
     * 统计机构分发量
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    List<OrgDistributeStatisticsBO> getOrgDistributeStatistics(String startDate, String endDate);

    /**机构分发汇总*/
    List<Map<String,Object>> getOrgDistributeCountSummation(String startDate, String endDate);

    /**
     * 获取机构分发成功，没有姓名的占比
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param orgId 机构id
     * @return Integer
     */
    @Deprecated
    Integer getDistributeSuccessNotNameCounts(String startDate, String endDate,Long orgId);

    List<Map<String,Object>> getChannelQualityForAverage(String startDate, String endDate);

    List<Map<String,Object>> getChannelQuality(String startDate, String endDate);

    List<Map<String,Object>> getlineLenderCountSummation(String startDate, String endDate);


    /**
     * 查询机构列表发送成功的用户
     * @param orgIdArray 数组，机构id数组
     * @return List<UserAptitudePO>
     */
    List<UserAptitudePO> getOrgDistributeSuccessUser(String startDate,String endDate,String...orgIdArray);


    List<Map<String,String>> getWechatChannelConversion(String startDate,String endDate);

}
