package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("apiSender_10071")
public class ZhenYing_TT_Api implements  ApiSender{

    private static final Logger log = LoggerFactory.getLogger(ZhenYing_TT_Api.class);

    private static final String url = "http://www.zykejii.com/service/importCustomer/addCustomerOnline";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【浙江臻硬资产-头条】分发异常:"+e.getMessage()));
            return new SendResult(false,"【浙江臻硬资产-头条】分发失败："+e.getMessage());
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){
        if(StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(po.getName())) {
                if(StringUtils.isBlank(parse.getString("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("name",po.getName());
        data.put("telephone",po.getMobile());
        data.put("hasCar", JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("hasHouse", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("hasInsurance", JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("hasCardSalary",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("hasProvidentFund",po.getPublicFund().contains("有，")?1:0);
        data.put("loanLimit", LoanAmountUtil.transform(po.getLoanAmount()).toString());
        data.put("source","头条");
        data.put("hasSocialSecurity",0);


        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"retCode":"017","retMsg":"客户信息申请频繁","retData":null}
        String result = HttpClientProxy.doPost(url, data, "UTF-8", 3000, httpHeader);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if("000".equals(resultJSON.getString("retCode"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【浙江臻硬资产】分发成功:"+result));
            return new SendResult(true,"【浙江臻硬资产-头条】分发成功："+result);
        }
        if("017".equals(resultJSON.getString("retCode"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【浙江臻硬资产】分发重复:"+result));
            return new SendResult(false,"【浙江臻硬资产-头条】分发重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【浙江臻硬资产】分发失败:"+result));
        return new SendResult(false,"【浙江臻硬资产-头条】分发失败："+result);
    }




}
