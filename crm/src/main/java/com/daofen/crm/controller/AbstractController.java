package com.daofen.crm.controller;

import com.daofen.crm.base.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractController.class);

    public ResultVO success(){
        return success("success",null);
    }
    public ResultVO success(Object data){
        return success("success",data);
    }
    public ResultVO success(String message,Object data){
        return success(ResultVO.ResultCode.SUCCESS,message,data);
    }
    public ResultVO success(String code,String message,Object data){
        ResultVO vo = new ResultVO(code,message,data);
        LOG.debug("机构返回数据：{}",vo.toString());
        return vo;
    }


    public ResultVO failed(){
        return failed("fail",null);
    }
    public ResultVO failed(Object data){
        return failed("fail",data);
    }
    public ResultVO failed(String message,Object data){
        return failed(ResultVO.ResultCode.FAIL,message,data);
    }
    public ResultVO failed(String code,String message,Object data){
        return new ResultVO(code,message,data);
    }


}
