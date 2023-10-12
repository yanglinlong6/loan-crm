package com.daofen.crm.base;

import com.daofen.crm.utils.JSONUtil;
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

    public ResultVO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultVO(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }


    public interface ResultCode{

        /**成功：0*/
        String SUCCESS = "0";

        /**失败：1*/
        String FAIL = "1";

        /**请登录：2*/
        String LOGIN = "2";
        
        /**请登录：2*/
        String NO_AUTH = "3";
    }
    
}
