package com.crm.service.channel;

import com.crm.common.PageBO;
import com.crm.service.channel.model.*;
import com.ec.v2.entity.trajectory.Page;

import java.util.List;

public interface ChannelService {

    /**
     * 渠道管理：分页
     * @param page PageBO<ChannelPO>
     */
    void getChannelPage(PageBO<ChannelPO> page);


    List<ChannelPO> getAllChannel();

    /**
     * 新增渠道
     * @param channelPO
     */
    void addChannel(ChannelPO channelPO);

    /**
     * 修改渠道
     * @param channelPO
     */
    void updateChannel(ChannelPO channelPO);

    /**
     * 删除渠道
     * 注意：后续删除要添加判断：判断改渠道是否有客户，如果有则不能删除，否则可删除
     * @param channelPO
     */
    void delChannel(ChannelPO channelPO);

    /**
     * 获取渠道对象
     * @param id 渠道id
     * @return ChannelPO
     */
    ChannelPO getChannel(Long id);

    /**
     * 渠道配量分页列表
     * @param pageBO PageBO<ChannelRationBO>
     */
    void getChannelRationPage(PageBO<ChannelRationBO> pageBO);

    /**
     * 新增渠道配量信息
     * @param channelRationPO ChannelRationPO
     */
    void addChannelRation(ChannelRationPO channelRationPO);

    /**
     * 编辑渠道配量信息
     * @param channelRationPO ChannelRationPO
     */
    void updatChannelRation(ChannelRationPO channelRationPO);


    /**
     * 获取渠道报表分页列表
     * @param pageBO PageBO<ChannelReportBO>
     * @return
     */
    void getChannelReportList(PageBO<ChannelReportBO> pageBO);

    /**
     *  获取配量成本列表
     * @param channelPrice ChannelPricePO
     * @return List<ChannelPricePO>
     */
    List<ChannelPricePO> getAllChannelPrice(ChannelPricePO channelPrice);

    /**
     * 增加渠道单价
     * @param channelPrice
     */
    void addChannelPrice(ChannelPricePO channelPrice);

    void updateChannelPrice(ChannelPricePO channelPrice);

    void delChannelPrice(Long id);

}
