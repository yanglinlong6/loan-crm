package com.loan.cps.service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AptWeightUtil;
import com.loan.cps.common.HttpUtil;
import com.loan.cps.common.LenderCache;
import com.loan.cps.common.LenderFilter;
import com.loan.cps.common.LenderMsgBuilder;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WXConstants;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.dao.LenderDao;
import com.loan.cps.entity.LenderPO;
import com.loan.cps.entity.NodeLogEntity;
import com.loan.cps.entity.Session;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.process.NodeManager;
import com.loan.cps.process.NodeResult;
import com.mysql.jdbc.JDBC4Connection;

@Component
public class IScheduledService implements ScheduledService{
	
	private static Log logger = LogFactory.getLog(IScheduledService.class);
	
	@Autowired
	private RedisTemplate<String, Object> template;
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private NodeManager nodeManager;
	
	@Autowired
	private LenderDao lenderDao;
	
	@Autowired
	private UserAptitudeService userAptitudeService;
	
	private static final String REDIS_SCHEDULED_LIST_KEY = "REDIS_CPS_SCHEDULED_LIST_KEY";
	
//	private static final String REDIS_SCHEDULED_LIST_KEY = "REDIS_CPS_TEST_SCHEDULED_LIST_KEY";
	
	private static final Integer NODE_LOG_JOB_START_STATE = 0;
	
	private static final Integer NODE_LOG_JOB_SECOND_STATE = 1;
	
	private static final Integer NODE_LOG_JOB_THREE_STATE = 2;

	@Override
	public void saveNodeResult(NodeResult nodeResult, Session session) {
		if(nodeResult.getState().equals(NodeResult.NODE_SUCCESS)&&nodeResult.getQualification()!=null) {
			JSONObject qualification = nodeResult.getQualification();
			UserAptitudePO userAptitudePO = userAptitudeService.get(session.getUserId());
			JSONObject parseObject2 = JSON.parseObject(JSON.toJSONString(userAptitudePO));
			parseObject2.putAll(qualification);
			UserAptitudePO parseObject = JSONObject.parseObject(parseObject2.toJSONString(), UserAptitudePO.class);
			parseObject.setWeight(AptWeightUtil.getAptWeight(parseObject));
			if(!StringUtils.isBlank(qualification.getString("mobile"))) {
				UserAptitudePO byMobile = userAptitudeService.getByMobile(parseObject.getMobile());
				if(byMobile != null) {
					session.setAiCount(999);
				}
			}
			if(session.getAiCount()==999) {
				parseObject.setLevel(10);
			}
			userAptitudeService.update(parseObject);
		}
		if(nodeResult.getState().equals(NodeResult.NODE_FAIL)) {
			return ;
		}
//		if(session.getFinish()!=null) {
//			return;
//		}
		template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, getNodeLogEntity(nodeResult,session));
		logger.info("node success result set queue "+JSON.toJSONString(nodeResult));
	}
	
	private NodeLogEntity getNodeLogEntity(NodeResult nodeResult, Session session) {
		NodeLogEntity entity = new NodeLogEntity();
		entity.setNodeId(session.getNodeId());
		entity.setState(NODE_LOG_JOB_START_STATE);
		entity.setSessionId(session.getSessionId());
		entity.setUserId(session.getUserId());
		entity.setTime(System.currentTimeMillis());
		return entity;
	}

	@Scheduled(fixedRate = 10*1000)
	public void run() {
		long currentTimeMillis2 = System.currentTimeMillis();
		while(System.currentTimeMillis()-currentTimeMillis2<9000) {
			try {
				NodeLogEntity decode = (NodeLogEntity) template.opsForList().rightPop(REDIS_SCHEDULED_LIST_KEY);
				if(decode!=null) {
					Session session = sessionService.getSession(decode.getUserId());
					if(session==null||!session.getSessionId().equals(decode.getSessionId())) {
						continue;
					}
					if(session.getUpstream_time()>decode.getTime()) {
						continue;
					}else {
						if(System.currentTimeMillis()/3600000%24>11) {
							template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, decode);
							continue;
						}
						long currentTimeMillis = System.currentTimeMillis();
						if(currentTimeMillis-decode.getTime()<5*60*1000l) {
							template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, decode);
							continue;
						}
						if(decode.getState().equals(NODE_LOG_JOB_THREE_STATE)&&currentTimeMillis-decode.getSecondTime()<90*60*1000l) {
							template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, decode);
							continue;
						}
						if(decode.getState().equals(NODE_LOG_JOB_SECOND_STATE)&&currentTimeMillis-decode.getSecondTime()<25*60*1000l) {
							template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, decode);
							continue;
						}
						if(decode.getState().equals(NODE_LOG_JOB_START_STATE)) {
							logger.info("send frist urge msg =  "+JSON.toJSONString(decode));
							if(session.getFinish()!=null||session.getNodeId().equals(NodeManager.FINISH3)||session.getNodeId().equals(NodeManager.FINISH4)||session.getNodeId().equals(NodeManager.NAME)) {
								JSONObject clone = (JSONObject)NodeManager.getNode(NodeManager.FIVE_MIN_URGE).getConfig().getRecommendMsg().clone();
								List<LenderPO> selByNoApplyList = lenderDao.selByNoApplyList(session.getUserId());
								if(selByNoApplyList.size()>0) {
									clone.put("content", String.format(clone.getString("content"),
											LenderMsgBuilder.buildLenderMsg(LenderFilter.filter2(session.getSettlement(), session,selByNoApplyList,3), session.getUserId(), session.getDomain2(),LenderMsgBuilder.FIVE_MIN)));
									WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "",
											session.getDomain2());
								}
							}else if(session.getNodeId().equals(NodeManager.FINISH5)||session.getNodeId().equals(NodeManager.FINISH2)){
								
							}else {
								NodeManager.getNode(NodeManager.FIVE_MIN_URGE).sendUrgeMsg(session);
							}
							decode.setState(NODE_LOG_JOB_SECOND_STATE);
							decode.setSecondTime(System.currentTimeMillis());
							template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, decode);
							continue;
						}
						if (decode.getState().equals(NODE_LOG_JOB_SECOND_STATE)&&currentTimeMillis - decode.getSecondTime() >= 60 * 60 * 1000l ) {
							logger.info("send second urge msg =  "+JSON.toJSONString(decode));
							if(session.getFinish()!=null||session.getNodeId().equals(NodeManager.FINISH3)||session.getNodeId().equals(NodeManager.FINISH4)||session.getNodeId().equals(NodeManager.NAME)||session.getNodeId().equals(NodeManager.FINISH5)||session.getNodeId().equals(NodeManager.FINISH2)) {
								JSONObject clone = (JSONObject)NodeManager.getNode(NodeManager.FINISH4).getConfig().getRecommendMsg().clone();
								clone.put("content", String.format(clone.getString("content"),session.getDomain2()));
									WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "",
											session.getDomain2());
							}else {
								NodeManager.getNode(NodeManager.ONE_HOUS_URGE).sendUrgeMsg(session);
								HttpUtil.getForObject("http://"+session.getDomain2()+"/dialogue/node/proceed2?userId="+session.getUserId());
							}
							decode.setState(NODE_LOG_JOB_THREE_STATE);
							decode.setSecondTime(System.currentTimeMillis());
							template.opsForList().leftPush(REDIS_SCHEDULED_LIST_KEY, decode);
							continue;
						}
						if (decode.getState().equals(NODE_LOG_JOB_THREE_STATE)&&currentTimeMillis - decode.getSecondTime() >= 180 * 60 * 1000l ) {
							logger.info("send three urge msg =  "+JSON.toJSONString(decode));
							if(session.getFinish()!=null||session.getNodeId().equals(NodeManager.FINISH3)||session.getNodeId().equals(NodeManager.FINISH4)||session.getNodeId().equals(NodeManager.NAME)) {
								JSONObject clone = (JSONObject)NodeManager.getNode(NodeManager.SECOND_HOUS_URGE).getConfig().getRecommendMsg().clone();
								List<LenderPO> selByNoApplyList = lenderDao.selByNoApplyList(session.getUserId());
								if(selByNoApplyList.size()>0) {
									clone.put("content", String.format(clone.getString("content"),
											LenderMsgBuilder.buildLenderMsg(LenderFilter.filter2(session.getSettlement(), session,selByNoApplyList,1), session.getUserId(), session.getDomain2(),LenderMsgBuilder.THREE_HOUS)));
									WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "",
											session.getDomain2());
								}
							}else if(session.getNodeId().equals(NodeManager.FINISH5)||session.getNodeId().equals(NodeManager.FINISH2)){
								
							}else {
								NodeManager.getNode(NodeManager.SECOND_HOUS_URGE).sendUrgeMsg(session);
								HttpUtil.getForObject("http://"+session.getDomain2()+"/dialogue/node/proceed2?userId="+session.getUserId());
							}
						}
					}
				}else {
					Thread.sleep(2000);
				}
			} catch (Exception e) {
			}
		}
	}
	
	@Scheduled(cron = "0 0 9 * * ?")
	public void job() {
		List<Session> sessionByDate = sessionService.getSessionByDate(-1);
		logger.info("send Yesterday urge msg size = "+sessionByDate.size());
		for(Session session:sessionByDate) {
			try {
				if(session.getFinish()!=null||session.getNodeId().equals(NodeManager.FINISH3)||session.getNodeId().equals(NodeManager.FINISH4)||session.getNodeId().equals(NodeManager.NAME)) {
					JSONObject clone = (JSONObject)NodeManager.getNode(NodeManager.FIVE_MIN_URGE).getConfig().getRecommendMsg().clone();
					List<LenderPO> selByNoApplyList = lenderDao.selByNoApplyList(session.getUserId());
					if(selByNoApplyList.size()>0) {
						clone.put("content", String.format(clone.getString("content"),
								LenderMsgBuilder.buildLenderMsg(LenderFilter.filter2(session.getSettlement(), session,selByNoApplyList,3), session.getUserId(), session.getDomain2(),LenderMsgBuilder.FIVE_MIN)));
						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "",
								session.getDomain2());
					}
				}else if(session.getNodeId().equals(NodeManager.FINISH5)||session.getNodeId().equals(NodeManager.FINISH2)){
					
				}else {
					NodeManager.getNode(NodeManager.ONE_HOUS_URGE).sendUrgeMsg(session);
					HttpUtil.getForObject("http://"+session.getDomain2()+"/dialogue/node/proceed2?userId="+session.getUserId());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		List<Session> sessionByDate2 = sessionService.getSessionByDate(-2);
		logger.info("send The day before yesterday urge msg size = "+sessionByDate.size());
		for(Session session:sessionByDate2) {
			try {
				if(session.getFinish()!=null||session.getNodeId().equals(NodeManager.FINISH3)||session.getNodeId().equals(NodeManager.FINISH4)||session.getNodeId().equals(NodeManager.NAME)) {
					JSONObject clone = (JSONObject)NodeManager.getNode(NodeManager.SECOND_HOUS_URGE).getConfig().getRecommendMsg().clone();
					List<LenderPO> selByNoApplyList = lenderDao.selByNoApplyList(session.getUserId());
					if(selByNoApplyList.size()>0) {
						clone.put("content", String.format(clone.getString("content"),
								LenderMsgBuilder.buildLenderMsg(LenderFilter.filter2(session.getSettlement(), session,selByNoApplyList,1), session.getUserId(), session.getDomain2(),LenderMsgBuilder.THREE_HOUS)));
						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(clone, session.getUserId()), "",
								session.getDomain2());
					}
				}else if(session.getNodeId().equals(NodeManager.FINISH5)||session.getNodeId().equals(NodeManager.FINISH2)){
					
				}else {
					NodeManager.getNode(NodeManager.SECOND_HOUS_URGE).sendUrgeMsg(session);
					HttpUtil.getForObject("http://"+session.getDomain2()+"/dialogue/node/proceed2?userId="+session.getUserId());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
}
