package com.daofen.admin.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    public ResultVO success(){
        return success(ResultCode.SUC);
    }
    public ResultVO success(ResultCode resultCode){
        return success(resultCode,resultCode.getDesc());
    }


    public ResultVO success(ResultCode resultCode,Object data){
        return success(resultCode.getCode(),resultCode.getDesc(),data);
    }

    public ResultVO success(String code,String message,Object data){
        return new ResultVO(code,message,data);
    }


    public ResultVO failed(){
        return failed(ResultCode.FAID);
    }
    public ResultVO failed(ResultCode resultCode){
        return failed(resultCode,resultCode.getDesc());
    }

    public ResultVO failed(ResultCode resultCode,String message){
        return failed(resultCode,message,null);
    }

    public ResultVO failed(ResultCode resultCode,String message,Object data){
        return new ResultVO(resultCode,message,data);
    }
    public ResultVO failed(String code,String message,Object data){
        return new ResultVO(code,message,data);
    }


}
