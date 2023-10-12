package com.daofen.crm.controller.callAccount;

import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.callAccount.CallAccountService;
import com.daofen.crm.service.callAccount.model.CallAccountPO;
import com.daofen.crm.utils.DateUtil;
import com.daofen.crm.utils.HttpUtil;
import com.daofen.crm.utils.JSONUtil;
import com.daofen.crm.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class CallAccountController {

    @Autowired
    private CallAccountService callAccountService;

    @RequestMapping("/call/account")
    @ResponseBody
    public ResultVO getAccount(@RequestParam() String mobile){
        if(StringUtils.isBlank(mobile) || mobile.length() < 11){
            return new ResultVO(ResultVO.ResultCode.FAIL,"手机号码必填") ;
        }
        CallAccountPO callAccountPO = callAccountService.getCallAccount(1l);

        String date  = DateUtil.formatToString(new Date(),"yyyyMMddHHmmss");


        String url = new StringBuffer()
                .append( callAccountPO.getHost())
                .append("/v20160818/call/dialout/")
                .append(callAccountPO.getAccountId())
                .append("?sig=").append(MD5Util.getMd5String(callAccountPO.getAccountId()+callAccountPO.getApiSecret()+date).toUpperCase())
                .toString();
        JSONObject data = new JSONObject();
        data.put("FromExten", LoginUtil.getLoginUser().getFromExten());
        data.put("Exten",mobile);
        data.put("ExtenType","Local");

        HttpHeaders headersJson = new HttpHeaders();
        headersJson.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headersJson.add("Authorization", Base64Util.encode(callAccountPO.getAccountId()+":"+ date));
        String response = HttpUtil.postForObject(url,data,headersJson);
        JSONObject json = JSONUtil.toJSON(response);
        if(json.getBooleanValue("Succeed")){
            return new ResultVO(ResultVO.ResultCode.SUCCESS,"呼叫成功") ;
        }
        return new ResultVO(ResultVO.ResultCode.FAIL,"呼叫失败",response) ;
    }



}
