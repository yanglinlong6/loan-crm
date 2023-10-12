package com.help.loan.distribute.controller;

import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.ResultVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 微信朋友圈广告数据上报授权接口
 */
@RestController
public class WechatAuthContronller {

    private static final Logger log = LoggerFactory.getLogger(WechatAuthContronller.class);


    @RequestMapping("/tencent/auth")
    @ResponseBody
    public ResultVO auth(@RequestParam("clientId") String clientId,
                         @RequestParam("accountId")String accountId,
                         @RequestParam("authorization_code")String authorizationCode){
        if(StringUtils.isBlank(clientId) || StringUtils.isBlank(accountId) || StringUtils.isBlank(authorizationCode)){
            return new ResultVO(ResultCode.FAILED,"认证失败:应用ID,广告主账号id,认证code,不能为空[非法请求]");
        }
        log.info("应用ID:{},广告主账号ID:{},auth_code:{}",clientId,accountId,authorizationCode);
        return new ResultVO(ResultCode.SUCCESS,"获取认证Code成功",authorizationCode);
    }



}
