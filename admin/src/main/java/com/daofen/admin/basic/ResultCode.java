package com.daofen.admin.basic;

import com.daofen.admin.utils.JSONUtil;

public enum  ResultCode {
    /**成功*/
    SUC("0","成功"),
    /**失败*/
    FAID("1","失败"),

    /**系统错误*/
    SYS_ERR("2","系统错误，请联系管理员，谢谢"),

    LOGIN_FAIL("3","登录失效，请重新登录");

    private String code;

    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    ResultCode() {
    }

    ResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}
