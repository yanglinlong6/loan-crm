package com.loan.wechat.user;


public interface UserAptitudeService {

	/**
     * 获取一定日期内的城市发送状态数量数量
	 * @param city 城市
	 * @param startDate 开始日期 例如：2021-11-22 00:00:00
	 * @param endDate   结束日期 例如：2021-11-22 23:59:59
	 * @param level     客户发送装填：如果为null，则标识查询所有
	 * @return int 数量
	 */
	int getCityCountByLevel(String city ,
							String startDate,
							String endDate,
							Integer level);

	/**
     * 获取一定日期内的渠道发送状态数量数量
	 * @param channel 渠道
	 * @param startDate 开始日期 例如：2021-11-22 00:00:00
	 * @param endDate 结束日期 例如：2021-11-22 23:59:59
	 * @param level 客户发送装填：如果为null，则标识查询所有
	 * @return int 数量
	 */
	int getChannelCountByLevel(String channel ,
							String startDate,
							String endDate,
							Integer level);


	UserAptitudePO getChannelUserAptitudePO(String channel ,
											String startDate,
											String endDate);
	
}
