package com.loan.cps.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.loan.cps.common.LenderCache;
import com.loan.cps.dao.LenderDao;

@Component
public class LenderService implements InitializingBean{
	
	@Autowired
	private LenderDao lenderDao;
	
	@Scheduled(cron = "0 */5 * * * ?")
	public void setCache() {
		LenderCache.setCacheList(lenderDao.selectAll());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LenderCache.setCacheList(lenderDao.selectAll());
		LenderCache.setDfLender(lenderDao.selectByLenderId("98ecfd3e182511ea947cec0d9a7d66aa"));
	}
	
}
