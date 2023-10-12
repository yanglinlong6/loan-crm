package com.crm.service.channel.dao;


import com.crm.common.PageBO;
import com.crm.service.channel.model.ChannelRationBO;
import com.crm.service.channel.model.ChannelRationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ChannelRationMapper {


    List<ChannelRationBO> selectChannelRationPage(PageBO<ChannelRationBO> pageBO);

    int selectChannelRationPageCount(PageBO<ChannelRationBO> pageBO);

    ChannelRationPO selectChannelRationPO(@Param("channelId") Long channelId,@Param("city") String city, @Param("media")String media);

    int insertChannelRation(ChannelRationPO ChannelRationPO);

    int updateChannelRationPO(ChannelRationPO record);

}