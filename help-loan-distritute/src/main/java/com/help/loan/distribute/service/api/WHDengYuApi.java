package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * 武汉登钰
 */
@Component("apiSender_20074")
public class WHDengYuApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(WHDengYuApi.class);

    private static final String checkUrl = "http://101.132.156.35/api/customer/check_mobile";
    private static final String sendUrl = "http://101.132.156.35/api/customer/import_data";
    private static final String password = "X65c2uAAJ3F";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            log.error("[武汉登钰]分发异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[武汉登钰]分发异常:" + e.getMessage()));
            return new SendResult(false, "[武汉登钰]分发异常" + e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) {

        // 撞库检查
        JSONObject data = new JSONObject();
        data.put("mobile", MD5Util.getMd5String(po.getMobile()));
        data.put("file_id", 15);
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"success":true,"message":"成功"}
        String checkResult = HttpClientProxy.doPost(checkUrl, data, "UTF-8", 3000, httpHeader);
        log.info("[武汉登钰]撞库检查结果:{}", checkResult);
        if (JSONUtil.toJSON(checkResult).getIntValue("code") != 0) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[武汉登钰]撞库重复:" + checkResult));
            return new SendResult(false, "[武汉登钰]撞库重复:" + checkResult);
        }
        isHaveAptitude(po);
        data.clear();
        data.put("sex", po.getGender());
        data.put("file_id", 15);
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        data.put("city", po.getCity());
        data.put("age", po.getAge());
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("is_house", JudgeUtil.in(po.getHouse(), 1, 2) ? 1 : 0);
        data.put("is_car", JudgeUtil.in(po.getCar(), 1, 2) ? 1 : 0);
        data.put("is_insurance", JudgeUtil.in(po.getInsurance(), 1, 2) ? 1 : 0);
        data.put("is_company", JudgeUtil.in(po.getCompany(), 1, 2) ? 1 : 0);
        data.put("is_fund", po.getPublicFund().contains("有，") ? 1 : 0);
        data.put("is_social", 0);
        data.put("is_tax", 0);
        data.put("is_credit", JudgeUtil.in(po.getCreditCard(), 1, 2) ? 1 : 0);
        data.put("webank", 0);
        data.put("is_work", JudgeUtil.in(po.getCompany(), 1) ? 1 : 0);
        data.put("passwd", password);
        //{"msg":"提交成功!","code":200}   {\"success\":false,\"message\":\"手机密文：f6b4852fff9c6630db76fd8048c3bebd已重复\"}","success":false}
        log.info("[武汉登钰]传输参数:{}", data.toJSONString());
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("[武汉登钰]分发结果:{}", result);
        JSONObject json = JSONUtil.toJSON(result);
        if (json.getIntValue("code") == 0) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[武汉登钰]分发成功:" + result));
            return new SendResult(true, "[武汉登钰]分发成功:" + result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[武汉登钰]分发失败:" + result));
        return new SendResult(false, "[武汉登钰]分发失败:" + result);

    }


    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试请忽略");
        po.setMobile("13649692802");
        po.setCity("武汉市");
        po.setLoanAmount("50000");
        po.setCompany(1);
        po.setPublicFund("没有公积金");
        po.setCar(1);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(1);
        po.setOccupation(1);
        po.setCreditCard(1);
        po.setAge(30);
        po.setGender(1);
        po.setUpdateDate(new Date());
        ApiSender api = new WHDengYuApi();
        System.out.println(api.send(po, null));
    }

}
