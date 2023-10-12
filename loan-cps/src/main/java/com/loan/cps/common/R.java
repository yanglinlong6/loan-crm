package com.loan.cps.common;

import org.apache.commons.lang3.StringUtils;

public class R {

	public static final String SUC = "0";

	public static final String FAIL = "1";
	
	private String code;
	
	private String msg;
	
	private Object data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public static R ok() {
		R r = new R();
		r.setCode(SUC);
		r.setMsg("suc");
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.setCode(SUC);
		r.setMsg(msg);
		return r;
	}

	public static R ok(String msg,Object data) {
		R r = new R();
		r.setCode(SUC);
		r.setMsg(msg);
		r.setData(data);
		return r;
	}

	public static R ok(Object data) {
		R r = new R();
		r.setCode(SUC);
		r.setMsg("成功");
		r.setData(data);
		return r;
	}
	
	public static R fail() {
		R r = new R();
		r.setCode(FAIL);
		r.setMsg("失败");
		return r;
	}

	public static R fail(String msg) {
		return fail(FAIL,StringUtils.isBlank(msg)?"失败":msg,null);
	}
	
	public static R fail(String code,String msg) {
		return fail(code,msg,null);
	}

	public static R fail(String code,String msg,Object data) {
		R r = new R();
		r.setCode(code);
		r.setMsg(msg);
		return r;
	}


	@Override
	public String toString() {
		return JSONUtil.toString(this);
	}
}
