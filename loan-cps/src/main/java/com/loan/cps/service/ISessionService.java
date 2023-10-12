package com.loan.cps.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.loan.cps.common.MongoDbUtils;
import com.loan.cps.entity.Session;
import com.loan.cps.process.NodeManager;
import com.mongodb.BasicDBObject;

@Component
public class ISessionService implements SessionService{
	
	@Autowired
	private RedisTemplate<String, Object> template;
	
	private static final String REDIS_SESSION_PREFIX = "ai_session_cps_";
	
	private static final String SESSION_COLLECTION_NAME = "ai_session_cps";
	
	private static Log logger = LogFactory.getLog(ISessionService.class);

	@Override
	public Session getSession(String userId) {
		Session decode = (Session) template.opsForValue().get(REDIS_SESSION_PREFIX+userId);
		if(decode == null) {
			Document findOne = MongoDbUtils.findOne("userId", userId, SESSION_COLLECTION_NAME);
			if(findOne != null) {
				decode = JSON.parseObject(findOne.getString("data"), Session.class);
				template.opsForValue().set(REDIS_SESSION_PREFIX+userId, decode, 1800, TimeUnit.SECONDS);
			}
		}
		return decode;
	}

	@Override
	public Session initSession(String userId,String domain2) {
		Session session = new Session();
		session.setAiCount(0);
		if("jingjiyong.zdt360.com".equals(domain2)) {
			session.setNodeId(NodeManager.DF_WELCOME);
		}else {
			session.setNodeId(NodeManager.WELCOME);
		}
		session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
		session.setUpstream_time(System.currentTimeMillis());
		session.setUserId(userId);
		session.setDomain2(domain2);
		return session;
	}

	@Override
	public Session setUpstreamTime(Session session) {
		session.setUpstream_time(System.currentTimeMillis());
		logger.info("user session = "+JSON.toJSONString(session));
		return session;
	}

	@Override
	public Session getSessionForProcess(String userId,String domain2) {
		Session session = getSession(userId);
		if(session == null) {
			session = initSession(userId,domain2);
		}
		return setUpstreamTime(session);
	}

	@Override
	public void saveSession(Session session) {
		template.opsForValue().set(REDIS_SESSION_PREFIX+session.getUserId(), session, 1800, TimeUnit.SECONDS);
		Document findOne = MongoDbUtils.findOne("userId", session.getUserId(), SESSION_COLLECTION_NAME);
		if(findOne!=null) {
			MongoDbUtils.del("userId", session.getUserId(), SESSION_COLLECTION_NAME);
		}else {
			findOne = new Document();
			findOne.put("createdDate", System.currentTimeMillis());
		}
		findOne.put("userId", session.getUserId());
		findOne.put("data", JSON.toJSONString(session));
		findOne.put("updatedDate", System.currentTimeMillis());
		MongoDbUtils.insert(findOne, SESSION_COLLECTION_NAME);
	}

	@Override
	public void delSession(String userId) {
		template.delete(REDIS_SESSION_PREFIX+userId);
		MongoDbUtils.del("userId", userId, SESSION_COLLECTION_NAME);
	}

	@Override
	public List<Session> getSessionByDate(Integer dateNum) {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DATE, dateNum);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String format2 = simpleDateFormat.format(instance.getTime());
		SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time=0;
		try {
			time = simpleDateFormat3.parse(format2).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BasicDBObject query = new BasicDBObject();
		Map<String, Object> queryMap = new HashMap<>(); 
		queryMap.put("$gt", time);
		queryMap.put("$lt", time + 24 * 60 * 60 * 1000);
		query.append("createdDate", new BasicDBObject(queryMap));
		List<Document> find = MongoDbUtils.find( query,  SESSION_COLLECTION_NAME);
		List<Session> list = new ArrayList<Session>();
		if(find == null) {
			return list;
		}
		for(Document d:find) {
			list.add(JSON.parseObject(d.getString("data"), Session.class));
		}
		return list;
	}
	
}
