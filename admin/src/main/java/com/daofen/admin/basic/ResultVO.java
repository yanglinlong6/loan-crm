package com.daofen.admin.basic;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
public class ResultVO {

    private String code;

    private String message;

    private Object data;

    public ResultVO() {
    }

    public ResultVO(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getDesc();
    }

    public ResultVO(ResultCode resultCode, Object data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getDesc();
        this.data = data;
    }

    public ResultVO(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultVO(ResultCode resultCode, String message, Object data) {
        this.code = resultCode.getCode();
        if(StringUtils.isBlank(message))  this.message = resultCode.getDesc();
        else this.message = message;
        this.data = data;
    }
}
