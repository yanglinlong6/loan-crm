package com.loan.wechat.login.entity;

import java.util.Date;

import com.loan.wechat.login.constant.LoginConstant;

public class Session {
	
	private String userId;
	
	private Date uplink;
	
	private Integer state;
	
	private Date startTime;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getUplink() {
		return uplink;
	}

	public void setUplink(Date uplink) {
		this.uplink = uplink;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public static Session init(String userId) {
		Session session = new Session();
		session.setUserId(userId);
		session.setStartTime(new Date());
		session.setUplink(new Date());
		session.setState(LoginConstant.Session.INIT_STATE);
		return session;
	}
	
}
