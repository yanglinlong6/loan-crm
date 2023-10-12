package com.help.loan.distribute.common;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResultVO {

    private String code;

    private String message;

    private Object data;

    public ResultVO() {
    }
    public ResultVO(String code) {
        this.code = code;
    }
    public ResultVO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultVO(String code, String message,Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }





    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
