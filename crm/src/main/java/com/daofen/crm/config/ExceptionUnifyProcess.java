package com.daofen.crm.config;

import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 公共统一异常处理器
 */
@ControllerAdvice
public class ExceptionUnifyProcess extends AbstractController {


    private static final Logger LOG = LoggerFactory.getLogger(ExceptionUnifyProcess.class);

    /**
     * 系统抛出的异常
     *
     * @param e Exception
     * @return ResultVO
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVO exceptionHandler(Exception e) {
        LOG.error(e.getMessage(), e);
        return this.failed(ResultVO.ResultCode.FAIL, "系统异常，请联系管理员，谢谢");
    }

    /**
     * 自定义异常
     *
     * @param e LoanMenuException
     * @return ResultVO
     */
    @ExceptionHandler(value = CrmException.class)
    @ResponseBody
    public ResultVO crmException(CrmException e) {
        LOG.error("系统抛出自定义异常：code【{}】，message【{}】", e.getCode(), e.getMessage(), e);
        return this.failed(e.getCode(), e.getMessage(),null);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultVO validatorException(MethodArgumentNotValidException e){
        LOG.error("接口参数验证异常：message【{}】", e.getMessage(), e);
        String[] array = e.getMessage().split(";");
        String message = array[array.length-1];
        return this.failed(ResultVO.ResultCode.FAIL, message,null);
    }

}
