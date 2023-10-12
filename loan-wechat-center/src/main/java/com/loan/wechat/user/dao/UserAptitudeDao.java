package com.loan.wechat.user.dao;

import com.loan.wechat.user.UserAptitudePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserAptitudeDao {


	Integer selectCityCountByLevel(@Param("city") String city ,
								   @Param("startDate") String startDate,
								   @Param("endDate") String endDate,
								   @Param("level") Integer level);

	Integer selectChannelCountByLevel(@Param("channel") String channel ,
							   @Param("startDate") String startDate,
							   @Param("endDate") String endDate,
							   @Param("level") Integer level);


	UserAptitudePO selectChannelUserAptitudePO(@Param("channel") String channel ,
											@Param("startDate") String startDate,
											@Param("endDate") String endDate);

}
