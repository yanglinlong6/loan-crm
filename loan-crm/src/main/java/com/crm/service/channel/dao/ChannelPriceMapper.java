package com.crm.service.channel.dao;


import com.crm.service.channel.model.ChannelPricePO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ChannelPriceMapper {

    int deleteChannelPrice(Long id);

    int insertChannelPrice(ChannelPricePO record);

    ChannelPricePO selectChannelPrice(Long id);

    /**
     * 查询成本列表
     * @param channelPricePO ChannelPricePO
     * @return List<ChannelPricePO>
     */
    List<ChannelPricePO> selectAllChannelPrice(ChannelPricePO channelPricePO);


    int updateChannelPrice(ChannelPricePO record);

    /**
     * 删除渠道,媒体,城市配量成本
     * @param id 删除渠道,媒体,城市配量单条成本id
     * @return 删除数量
     */
    int delChannelPrice(Long id);
}