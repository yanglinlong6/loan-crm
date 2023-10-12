package com.loan.cps.service;

import com.loan.cps.common.R;
import com.loan.cps.entity.UserAptitudePO;

public interface UserAptitudeService {

	UserAptitudePO get(String userId);
	
	void update(UserAptitudePO po);
	
	R add(UserAptitudePO po);

	/**
	 * 接收省呗客户
	 * @param po UserAptitudePO
	 * @return R
	 */
	R addShengBei(UserAptitudePO po);

	/**
	 * 保存信用逾期客户
	 * @param po
	 * @return
	 */
	R addCredit(UserAptitudePO po);
	
	UserAptitudePO getByMobile(String userId);
	
}
