package com.daofen.admin.basic;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * cpa异常处理
 *
 * @author zhangqiuping
 */
@Setter
@Getter
public class AdminException extends RuntimeException {

    private static final long serialVersionUID = -5890979639344423404L;

    private String code;

    private String message;

    private Object data;

    public AdminException(ResultCode code) {
        this.code = code.getCode();
        this.message = code.getDesc();
    }

    public AdminException(ResultCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }

    public AdminException(ResultCode code, String message, Object data) {
        this.code = code.getCode();
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
