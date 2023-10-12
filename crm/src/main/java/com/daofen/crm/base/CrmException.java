package com.daofen.crm.base;

import com.alibaba.fastjson.JSONObject;

/**
 * cpa异常处理
 *
 * @author zhangqiuping
 */
public class CrmException extends RuntimeException {

    private static final long serialVersionUID = -5890979639344423404L;

    private String code;

    private String message;

    private Object data;

    public CrmException(String code, String message) {
        super(code);
        this.code = code;
        this.message = message;
    }

    public CrmException(String code, String message, Object data) {
        super(code);
        this.code = code;
        this.message = message;
        this.data = data;
    }

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

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
