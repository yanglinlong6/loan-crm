package com.loan.cps.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.dao.SessionLogDao;
import com.loan.cps.entity.Session;
import com.loan.cps.process.NodeManager;
import com.loan.cps.process.NodeResult;

@Component
public class IDialogueFacade implements DialogueFacade{
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private ScheduledService scheduledService;
	
	@Autowired
	private NodeManager nodeManager;
	
	@Autowired
	private SessionLogDao sessionLogDao;
	
	@Override
	public void exc(JSONObject userMsg) {
		Session sessionForProcess = sessionService.getSessionForProcess(userMsg.getString("FromUserName"),
				userMsg.getString("domain2"));
		NodeResult noderesult = nodeManager.getNode(sessionForProcess.getNodeId()).answerHandle(sessionForProcess, userMsg);
		scheduledService.saveNodeResult(noderesult, sessionForProcess);
		sessionService.saveSession(sessionForProcess);
	}

	@Override
	public void start(JSONObject userMsg) {
		Session sessionForProcess = sessionService.getSessionForProcess(userMsg.getString("FromUserName"),
				userMsg.getString("domain2"));
		Integer integer = userMsg.getInteger("subCount");
		nodeManager.getNode(sessionForProcess.getNodeId()).sendQuestion(sessionForProcess);
		NodeResult noderesult = new NodeResult();
		noderesult.setState(NodeResult.NODE_SUCCESS);
		scheduledService.saveNodeResult(noderesult, sessionForProcess);
		sessionService.saveSession(sessionForProcess);
	}

	@Override
	public void end(JSONObject userMsg) {
		Session session = sessionService.getSession(userMsg.getString("FromUserName"));
		if (session != null) {
			sessionLogDao.save(session);
			sessionService.delSession(userMsg.getString("FromUserName"));
		}
	}

	@Override
	public void proceed(String userId, String domain2) {
		Session sessionForProcess = sessionService.getSessionForProcess(userId, domain2);
		nodeManager.getNode(sessionForProcess.getNodeId()).sendQuestion(sessionForProcess);
		NodeResult noderesult = new NodeResult();
		noderesult.setState(NodeResult.NODE_SUCCESS);
		scheduledService.saveNodeResult(noderesult, sessionForProcess);
		sessionService.saveSession(sessionForProcess);
	}
	
	@Override
	public void proceed2(String userId) {
		Session sessionForProcess = sessionService.getSession(userId);
		if(sessionForProcess == null) {
			return ;
		}
		nodeManager.getNode(sessionForProcess.getNodeId()).sendQuestion(sessionForProcess);
	}

}
