package com.loan.wechat.common;

public class Result {
	
	public static final String SUCCESS_CODE = "0";
	
	public static final String SUCCESS_MSG = "success";

	public static final String FAIL_CODE = "111111";
	
	public static final String FAIL_MSG = "fail";
	
	private String code;
	
	private String msg;
	
	private Object data;
	
	public Result() {
	}
	
	public Object getO() {
		return data;
	}

	public void setO(Object o) {
		this.data = o;
	}

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

	public Result(String code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}
	
	public Result(String code, String msg, Object o) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = o;
	}
	
	public static Result success() {
		return new Result(Result.SUCCESS_CODE,Result.SUCCESS_MSG);
	}

	public static Result success(Object o) {
		return new Result(Result.SUCCESS_CODE,Result.SUCCESS_MSG,o);
	}
	
	public static Result success(String code,String msg,Object o) {
		return new Result(code,msg,o);
	}
	
	public static Result fail() {
		return new Result(Result.FAIL_CODE,Result.FAIL_MSG);
	}
	
	public static Result fail(String code,String msg) {
		return new Result(code,msg);
	}
}
