package com.loan.wechat.login.entity;

import java.util.Date;

public class UserDTO {
	
	private Integer id;
	
	private String userId;
	
	private String openid;
	
	private String unionId;
	
	private Integer media;
	
	private Integer wxType;
	
	private Integer state;
	
	private Integer type;
	
	private Date followTime;
	
	private Date unfollowTime;
	
	private Integer subCount;
	
	private Integer sessionCount;
	
	private Integer jobState;
	
	private String createBy;
	
	private Date createDate;
	
	private String updateBy;
	
	private Date updateDate;
	
	public Integer getJobState() {
		return jobState;
	}

	public void setJobState(Integer jobState) {
		this.jobState = jobState;
	}

	public Integer getSubCount() {
		return subCount;
	}

	public void setSubCount(Integer subCount) {
		this.subCount = subCount;
	}

	public Integer getSessionCount() {
		return sessionCount;
	}

	public void setSessionCount(Integer sessionCount) {
		this.sessionCount = sessionCount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public Integer getMedia() {
		return media;
	}

	public void setMedia(Integer media) {
		this.media = media;
	}

	public Integer getWxType() {
		return wxType;
	}

	public void setWxType(Integer wxType) {
		this.wxType = wxType;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getFollowTime() {
		return followTime;
	}

	public void setFollowTime(Date followTime) {
		this.followTime = followTime;
	}

	public Date getUnfollowTime() {
		return unfollowTime;
	}

	public void setUnfollowTime(Date unfollowTime) {
		this.unfollowTime = unfollowTime;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
