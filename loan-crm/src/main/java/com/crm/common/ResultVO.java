package com.crm.common;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 接口返回对象
 */
@Setter
@Getter
public class ResultVO {

    private int code;

    private String message;

    private Object data;

    public ResultVO(){}

    public ResultVO(int code,String message,Object data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResultVO success(String message,Object data){
        return new ResultVO(CrmConstant.ResultCode.SUCCESS,message,data);
    }

    public static ResultVO fail(String message,Object data){
        return fail(CrmConstant.ResultCode.FAIL,message,data);
    }

    public static ResultVO fail(int code,String message,Object data){
        return new ResultVO(code,message,data);
    }


    @Override
    public String toString() {
        if(null == this)
            return "ResultVO{}";
        return JSONUtil.toJSONString(this);
    }
}
