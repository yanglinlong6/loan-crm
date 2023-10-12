package com.help.loan.distribute.controller;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.ResultVO;
import com.help.loan.distribute.schedule.CpaScheduleApi;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.org.OrgService;
import com.help.loan.distribute.service.org.model.OrgBO;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分发接口
 */
@RestController
public class DistributeController {

    private static final Logger log = LoggerFactory.getLogger(DistributeController.class);

    @Autowired
    private UserAptitudeDao dao;

    @Autowired
    private CpaScheduleApi cpaScheduleApi;


    @GetMapping("/send")
    @ResponseBody
    public ResultVO distribute(@RequestParam("userId") String userId){
        log.info("接口分发,收到用户：【{}】",userId);
        if(StringUtils.isBlank(userId))
            return new ResultVO(ResultCode.FAILED,"用户编号不能为空");
        UserAptitudePO userAptitude = dao.get(userId);
        if(null == userAptitude || 7 != userAptitude.getLevel().intValue()) // 7：标识表单客户
            return new ResultVO(ResultCode.FAILED,"用户不存在");
        log.info("接口分发,用户：【{}】【开始挑选机构】",userId);
        if(StringUtils.isNotBlank(userAptitude.getName()) && userAptitude.getName().contains("测试")){
            return new ResultVO(ResultCode.SUCCESS,"用户是测试数据");
        }
        JSONObject orgId = cpaScheduleApi.send5(userAptitude);
        if(orgId.getIntValue("isSuccess")==2 )
            return new ResultVO(ResultCode.FAILED,"",orgId.getString("orgId"));
        return new ResultVO(ResultCode.SUCCESS,null,orgId.getString("orgId"));
    }

    @Autowired
    OrgService orgService;

    @GetMapping("/send2")
    @ResponseBody
    public ResultVO distribute2(@RequestParam("userId") String userId){
        log.info("接口分发,收到用户：【{}】",userId);
        if(StringUtils.isBlank(userId))
            return new ResultVO(ResultCode.FAILED,"用户编号不能为空");
        UserAptitudePO userAptitude = dao.get(userId);
        if(null == userAptitude || 7 != userAptitude.getLevel().intValue()) // 7：标识表单客户
            return new ResultVO(ResultCode.FAILED,"用户不存在");
        log.info("接口分发,用户：【{}】【开始挑选机构】",userId);
        if(StringUtils.isNotBlank(userAptitude.getName()) && userAptitude.getName().contains("测试")){
            return new ResultVO(ResultCode.SUCCESS,"测试客户");
        }
        JSONObject result = cpaScheduleApi.send5(userAptitude);
        Integer isSuccess = result.getInteger("isSuccess");
        if(JudgeUtil.in(isSuccess,2) )
            return new ResultVO(isSuccess.toString(),"重复");
        if(JudgeUtil.in(isSuccess,3) )
            return new ResultVO(isSuccess.toString(),"城市不符");
        OrgBO orgBO = orgService.getOrgBO(Long.valueOf(result.getString("orgId")));
        return new ResultVO(ResultCode.SUCCESS,isSuccess.toString(),orgBO.getOrgName());
    }

    @GetMapping("/distribute/job")
    @ResponseBody
    public ResultVO distributeJob(){
        cpaScheduleApi.send();
        return new ResultVO(ResultCode.SUCCESS);
    }

}
