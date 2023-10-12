package com.crm.service.channel.dao;

import com.crm.common.PageBO;
import com.crm.service.channel.model.ChannelReportBO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 渠道包边dao
 */
@Component
@Mapper
public interface ChannelReportMapper {


    List<ChannelReportBO> getChannelReportByPage(PageBO<ChannelReportBO> page);

    int getChannelReportCountByPage(PageBO<ChannelReportBO> page);

    /**
     * 查询机构下的渠道，媒体，城市的星级数量
     * @param orgId 机构id
     * @param channelId 渠道id
     * @param media 媒体名称
     * @param city 城市
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 星级数量
     */
    int selectLevelCount(@Param("orgId") Long orgId, @Param("channelId")Long channelId,
                         @Param("media")String media, @Param("city")String city,
                         @Param("startDate")String startDate, @Param("endDate")String endDate,@Param("level")String level);

    /**
     * 有效或者无效客户数量
     * @param orgId 机构id
     * @param channelId 渠道id
     * @param media 媒体名称
     * @param city 城市
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param fit  有效客户 || 无效客户
     * @return 有效或者无效客户数量
     */
    int selectFitCount(@Param("orgId") Long orgId, @Param("channelId")Long channelId,
                         @Param("media")String media, @Param("city")String city,
                         @Param("startDate")String startDate, @Param("endDate")String endDate,@Param("fit")Byte fit);

    /**
     * 通话状态数量
     * @param orgId 机构id
     * @param channelId 渠道id
     * @param media 媒体名称
     * @param city 城市
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param call  通话状态
     * @return 通话状态数量
     */
    int selectCallCount(@Param("orgId") Long orgId, @Param("channelId")Long channelId,
                       @Param("media")String media, @Param("city")String city,
                       @Param("startDate")String startDate, @Param("endDate")String endDate,@Param("call")Byte call);

    /**
     * 汇总一定日期内的渠道,城市,媒体的合同金额
     * @param orgId 机构id
     * @param channelId 渠道id
     * @param media 媒体
     * @param city 城市
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return  double 汇总一定日期内的渠道,城市,媒体的合同金额
     */
    double selectContractAmount(@Param("orgId") Long orgId, @Param("channelId")Long channelId,
                        @Param("media")String media, @Param("city")String city,
                        @Param("startDate")String startDate, @Param("endDate")String endDate);


    /**
     * 汇总一定日期内的渠道,城市,媒体的进件金额
     * @param orgId 机构id
     * @param channelId 渠道id
     * @param media 媒体
     * @param city 城市
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return  double 汇总一定日期内的渠道,城市,媒体的进件金额
     */
    double selectImportAmount(@Param("orgId") Long orgId, @Param("channelId")Long channelId,
                              @Param("media")String media, @Param("city")String city,
                              @Param("startDate")String startDate, @Param("endDate")String endDate);

    double selectChannelConsumeAmount(@Param("orgId") Long orgId, @Param("channelId")Long channelId,
                                      @Param("media")String media, @Param("city")String city,
                                      @Param("startDate")String startDate, @Param("endDate")String endDate);
}
