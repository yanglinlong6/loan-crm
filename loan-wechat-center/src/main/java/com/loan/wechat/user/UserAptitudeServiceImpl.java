package com.loan.wechat.user;

import com.loan.wechat.user.dao.UserAptitudeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserAptitudeServiceImpl implements UserAptitudeService{

	@Autowired
	private UserAptitudeDao userAptitudeDao;


	@Override
	public int getCityCountByLevel(String city, String startDate, String endDate, Integer level) {
		if(null == userAptitudeDao || StringUtils.isEmpty(city) || StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate))
			return 0;
		Integer count = userAptitudeDao.selectCityCountByLevel(city,startDate,endDate,level);
		if(null == count)
			return 0;
		return count;
	}

	@Override
	public int getChannelCountByLevel(String channel, String startDate, String endDate, Integer level) {
		if(null == userAptitudeDao || StringUtils.isEmpty(channel) || StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate))
			return 0;
		Integer count = userAptitudeDao.selectChannelCountByLevel(channel,startDate,endDate,level);
		if(null == count)
			return 0;
		return count;
	}

	@Override
	public UserAptitudePO getChannelUserAptitudePO(String channel, String startDate, String endDate) {
		if(null == userAptitudeDao || StringUtils.isEmpty(channel) || StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate))
			return null;
		UserAptitudePO userAptitudePO = userAptitudeDao.selectChannelUserAptitudePO(channel,startDate,endDate);
		if(null == userAptitudePO){
			userAptitudePO = new UserAptitudePO();
			userAptitudePO.setInCity(0);
			return userAptitudePO;
		}
		return userAptitudePO;
	}
}
