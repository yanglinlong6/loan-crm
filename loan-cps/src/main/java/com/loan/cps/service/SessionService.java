package com.loan.cps.service;

import java.util.Date;
import java.util.List;

import com.loan.cps.entity.Session;

public interface SessionService {

	Session getSession(String userId);
	
	Session initSession(String userId,String domain2);
	
	Session setUpstreamTime(Session session);
	
	Session getSessionForProcess(String userId,String domain2);
	
	void saveSession(Session session);
	
	void delSession(String userId);
	
	List<Session> getSessionByDate(Integer dateNum);
}
