package com.loan.cps.common;

import org.apache.commons.lang3.StringUtils;

public class ZxfR {

    public static final String SUC = "0";

    public static final String FAIL = "1";

    private String code;

    private String message;

    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ZxfR ok() {
        ZxfR r = new ZxfR();
        r.setCode(SUC);
        r.setMessage("success");
        return r;
    }

    public static ZxfR ok(String message) {
        ZxfR r = new ZxfR();
        r.setCode(SUC);
        r.setMessage(message);
        return r;
    }

    public static ZxfR ok(String message, Object data) {
        ZxfR r = new ZxfR();
        r.setCode(SUC);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    public static ZxfR ok(Object data) {
        ZxfR r = new ZxfR();
        r.setCode(SUC);
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }

    public static ZxfR fail() {
        ZxfR r = new ZxfR();
        r.setCode(FAIL);
        r.setMessage("操作失败");
        return r;
    }

    public static ZxfR fail(String message) {
        return fail(FAIL, StringUtils.isBlank(message) ? "操作失败" : message, null);
    }

    public static ZxfR fail(String code, String message) {
        return fail(code, message, null);
    }

    public static ZxfR fail(String code, String message, Object data) {
        ZxfR r = new ZxfR();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }


    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
