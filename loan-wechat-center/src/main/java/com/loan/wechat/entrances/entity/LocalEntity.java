package com.loan.wechat.entrances.entity;

import java.io.Serializable;

public class LocalEntity implements Serializable{
	
	/**
	 * openid
	 */
	private String openid;
	/**
	 * 地理位置维度
	 */
	private double x;
	/**
	 * 地理位置经度
	 */
	private double y;
	
	/**
	 * 地图缩放大小（Scale）
	 */
	private double s;
	
	/**
	 * 时间戳
	 */
	private long t;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}
	public double getS() {
		return s;
	}
	public void setS(double s) {
		this.s = s;
	}
	@Override
	public String toString() {
		return "LocalEntity [openid=" + openid + ", x=" + x + ", y=" + y + ", s=" + s + ", t=" + t + "]";
	}
	
	
}
