package com.crm.config;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);


    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO exceptionHandler(Exception e){
        log.error("未知异常：{}",e.getMessage(),e);
        return ResultVO.fail("未知异常，请联系管理员",null);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ResultVO exceptionHandler(RuntimeException e){
        log.error("运行异常：{}-{}","运行错误,请联系管理员",e.getMessage(),e);
        if(e instanceof CrmException){
            CrmException crmException = (CrmException)e;
            return ResultVO.fail(crmException.getCode(),crmException.getMessage(),null);
        }
        return ResultVO.fail(CrmConstant.ResultCode.EX,e.getMessage(),null);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(value = CrmException.class)
    @ResponseBody
    public ResultVO exceptionHandler(CrmException e){
        log.error("CrmException:code-{},message-{}",e.getCode(),e.getMessage());
        return ResultVO.fail(e.getCode(),e.getMessage(),null);
    }
}
