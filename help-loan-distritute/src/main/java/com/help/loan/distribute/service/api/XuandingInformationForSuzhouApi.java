package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  苏州轩鼎信息技术有限公司对接文档 20139
 */
@Component("apiSender_20139")
public class XuandingInformationForSuzhouApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(XuandingInformationForSuzhouApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[苏州轩鼎信息技术有限公司对接文档]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[苏州轩鼎信息技术有限公司对接文档]推送异常:" + e.getMessage()));
            return new SendResult(false, "[苏州轩鼎信息技术有限公司对接文档]推送异常:" + e.getMessage());
        }
    }

    /**
     * 济南
     *
     * @param po
     * @param select
     * @return
     */
    private SendResult sendResultForJinan(UserAptitudePO po, UserDTO select) {
        String checkURL = String.format("http://106.14.157.223:8098/Customer/VerifyData?md5Mobile=%s", MD5Util.getMd5String(po.getMobile()));
        String checkResult = HttpClientProxy.doGet(checkURL, new JSONObject());
        log.info("[苏州轩鼎信息技术有限公司对接文档]撞库结果:{}", checkResult);
        if (JSONUtil.toJSON(checkResult).getBoolean("IsSuccess").equals(Boolean.FALSE)) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[苏州轩鼎信息技术有限公司对接文档]推送重复:" + checkResult));
            return new SendResult(false, "[苏州轩鼎信息技术有限公司对接文档]撞库重复:" + checkResult);
        }
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("Mobile", po.getMobile());
        data.put("CustomerName", po.getName());
        data.put("age", po.getAge());
        data.put("sex", po.getGender());
        data.put("ApplyCity", po.getCity());
        data.put("ApplyAmount", po.getLoanAmount());
        data.put("WagesType", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "1" : "3");
        data.put("IsHouse", JudgeUtil.in(po.getHouse(), 1, 2) ? 1 : 2);
        data.put("IsCar", JudgeUtil.in(po.getCar(), 1) ? 1 : 2);
        data.put("IsInsurance", JudgeUtil.in(po.getInsurance(), 1, 2) ? 1 : 2);
        data.put("Social", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? 1 : 2);
        data.put("Provident", JudgeUtil.in(po.getPublicFund(), "无", "0", "没有") ? 2 : 1);
        // TODO: 2023/10/13 具体需要和对接人确定
        data.put("source", "");
        data.put("OperatorName", "");
        data.put("Bundle", "");
        data.put("Amount", "");
        data.put("SexName", "");
        System.out.println(data.toJSONString());

        String url = "http://106.14.157.223:8098/Customer/Add";
        String result = restTemplate.postForObject(url, data, String.class);
        log.info("[苏州轩鼎信息技术有限公司对接文档]推送结果:{}", result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        Boolean isSuccess = jsonObject.getBoolean("IsSuccess");
        // IsSuccess true 表示成功 false表示失败
        if (isSuccess) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[苏州轩鼎信息技术有限公司对接文档]推送成功:" + result));
            return new SendResult(true, "[苏州轩鼎信息技术有限公司对接文档]推送成功:" + result);
        } else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[苏州轩鼎信息技术有限公司对接文档]推送失败:" + result));
        }
        return new SendResult(false, "[苏州轩鼎信息技术有限公司对接文档]推送失败:" + result);
    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试渠道");
        po.setMobile("13561691597");
        po.setCity("济南市");
        po.setLoanAmount("5-10万");
        po.setCompany(0);
        po.setPublicFund("没有公积金");
        po.setCar(0);
        po.setHouse(0);
        po.setInsurance(0);
        po.setGetwayIncome(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(34);
        po.setGender(2);
        po.setChannel("ttt-zxf-jn-jv60");
        po.setUpdateDate(new Date());
        ApiSender api = new XuandingInformationForSuzhouApi();
        System.out.println(api.send(po, null));
    }

}
