package com.loan.cps.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.loan.cps.api.Bank51Api;
import com.loan.cps.api.DDWApi;
import com.loan.cps.api.GLWApi;
import com.loan.cps.api.GLWApi2;
import com.loan.cps.api.GLWApi3;
import com.loan.cps.api.GLWApi4;
import com.loan.cps.api.GLWApi5;
import com.loan.cps.api.JXDApi;
import com.loan.cps.api.SendResult;
import com.loan.cps.dao.DispatcheRecDao;
import com.loan.cps.dao.UserAptitudeDao;
import com.loan.cps.dao.WechatUserBindDao;
import com.loan.cps.entity.DispatcheRecPO;
import com.loan.cps.entity.Session;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;

@Component
public class ICpaAPIScheduledService implements CpaAPIScheduledService{

	@Autowired
	private UserAptitudeDao dao;
	
	@Autowired
	private Bank51Api bank51Api;
	
	@Autowired
	private JXDApi jxdApi;
	
	@Autowired
	private GLWApi glwApi;
	
	@Autowired
	private GLWApi2 glwApi2;
	
	@Autowired
	private GLWApi3 glwApi3;
	
	@Autowired
	private GLWApi4 glwApi4;
	
	@Autowired
	private GLWApi5 glwApi5;
	
	@Autowired
	private DDWApi ddwApi;
	
	@Autowired
	private DispatcheRecDao dispatcheRecDao;
	
	private static Log logger = LogFactory.getLog(ICpaAPIScheduledService.class);
	
	@Autowired
	private WechatUserBindDao wechatUserBindDao;
	
	@Autowired
	private SessionService sessionService;
	
//	@Scheduled(fixedRate = 5*1000)
	@Override
	public void send() {
		List<UserAptitudePO> byLevel = dao.getByLevel(4);
		for(UserAptitudePO po:byLevel) {
			logger.info("send rec API = " +JSON.toJSONString(po));
			UserDTO select = wechatUserBindDao.select(po.getUserId());
			SendResult send = null;
			long orgId = 0;
			try {
				if(select.getWxType()==6) {
					send = glwApi5.send(po,select);
					orgId=10002l;
				}
				else if(select.getWxType()==4) {
					send = ddwApi.send(po,select);
					orgId=10003l;
				}
				else if(select.getWxType()==5||select.getWxType()==10) {
					send = jxdApi.send(po,select);
					orgId=10001l;
				}
				else if(select.getWxType()==7) {
					send = glwApi2.send(po,select);
					orgId=10002l;
				}
				else if(select.getWxType()==9) {
					send = glwApi.send(po, select);
					orgId=10002l;
				}else {
					send = glwApi.send(po, select);
					orgId=10002l;
				}
			} catch (Exception e) {
				logger.info(e);
				continue;
			}
			UserAptitudePO updateParams = new UserAptitudePO();
			int success = 0;
			if(send.isSuccess()) {
				updateParams.setLevel(5);
				success = 1;
			}else {
				updateParams.setLevel(6);
			}
			updateParams.setUserId(po.getUserId());
			dao.update(updateParams);
			dispatcheRecDao.add(getDispatcheRecPO(orgId,po.getId(),success,send.getResultMsg()));
		}
	}
	
	private DispatcheRecPO getDispatcheRecPO(Long orgId,Long custId,Integer status,String result) {
		DispatcheRecPO po = new DispatcheRecPO();
		Date date = new Date();
		po.setCreateDate(date);
		po.setUpdateDate(date);
		po.setCustomerId(custId);
		po.setOrgId(orgId);
		po.setDispatchStatus(status);
		po.setDispatchResult(result);
		return po;
	}
	
//	@Scheduled(fixedRate = 30*1000)
	public void send2() {
		List<UserAptitudePO> byLevel = dao.getByLevel(2);
		for(UserAptitudePO po:byLevel) {
			Session session =null;
			try {
				session = sessionService.getSession(po.getUserId());
				if(session != null&&System.currentTimeMillis()-session.getUpstream_time()<35*60*1000) {
					continue;
				}
			}catch (Exception e) {
				logger.info(e);
			}
			logger.info("send rec 2 API = " +JSON.toJSONString(po));
			UserDTO select = wechatUserBindDao.select(po.getUserId());
			SendResult send = null;
			long orgId = 0;
			try {
				if(select.getWxType()==6) {
					send = glwApi5.send(po,select);
					orgId=10002l;
				}
				else if(select.getWxType()==4) {
					send = ddwApi.send(po,select);
					orgId=10003l;
				}
				else if(select.getWxType()==5||select.getWxType()==10) {
					send = jxdApi.send(po,select);
					orgId=10001l;
				}
				else if(select.getWxType()==7) {
					send = glwApi2.send(po,select);
					orgId=10002l;
				}
				else if(select.getWxType()==9) {
					send = glwApi.send(po, select);
					orgId=10002l;
				}else {
					send = glwApi.send(po, select);
					orgId=10002l;
				}
			} catch (Exception e) {
				logger.info(e);
				continue;
			}
			UserAptitudePO updateParams = new UserAptitudePO();
			int success = 0;
			if(send.isSuccess()) {
				updateParams.setLevel(5);
				success = 1;
			}else {
				updateParams.setLevel(6);
			}
			updateParams.setUserId(po.getUserId());
			dao.update(updateParams);
			dispatcheRecDao.add(getDispatcheRecPO(orgId,po.getId(),success,send.getResultMsg()));
		}
	}

}
