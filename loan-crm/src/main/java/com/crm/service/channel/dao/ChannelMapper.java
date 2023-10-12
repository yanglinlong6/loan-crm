package com.crm.service.channel.dao;


import com.crm.common.PageBO;
import com.crm.service.channel.model.ChannelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ChannelMapper {


    List<ChannelPO> selectChannelPage(PageBO<ChannelPO> page);

    int selectChannelPageCount(PageBO<ChannelPO> page);


    List<ChannelPO> selectAll(@Param("orgId")Long orgId,@Param("channelId")Long channelId);

    int insertChannel(ChannelPO channel);

    ChannelPO selectChannelById(Long id);

    ChannelPO selectChannelByNicknameOrCompany(@Param("orgId") Long orgId,@Param("nickname") String nickname, @Param("company") String company);

    int updateBChannel(ChannelPO channel);

    int delChannel(Long id);

}