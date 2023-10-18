package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.AESUtil;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  青岛极达信息技术咨询服务有限公司 20141
 */
@Component("apiSender_20141")
public class JidaInformationForQingdaoApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(JidaInformationForQingdaoApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[青岛极达信息技术咨询服务有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[青岛极达信息技术咨询服务有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[青岛极达信息技术咨询服务有限公司]推送异常:" + e.getMessage());
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
        JSONObject checkData = new JSONObject();
        checkData.put("channelCode", "YUE_FU");
        checkData.put("phone", MD5Util.getMd5String(po.getMobile()));
        String checkURL = "https://crm-api.kairyun.com/api/common/hitCustomer";
        String checkResult = HttpUtil.postForJSON(checkURL, checkData);
        log.info("[青岛极达信息技术咨询服务有限公司]撞库结果:{}", checkResult);
        if (JSONUtil.toJSON(checkResult).getIntValue("code") != 200) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[青岛极达信息技术咨询服务有限公司]推送重复:" + checkResult));
            return new SendResult(false, "[青岛极达信息技术咨询服务有限公司]撞库重复:" + checkResult);
        }

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name", po.getName());
        data.put("phone", po.getMobile());
        data.put("gjj", JudgeUtil.contain(po.getPublicFund(), "有，") ? "1" : "0");
        data.put("house", JudgeUtil.in(po.getHouse(), 1, 2) ? 1 : 0);
        data.put("socialSecurity", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "1" : "0");
        data.put("businessInsurance", JudgeUtil.in(po.getInsurance(), 1, 2) ? "1" : "0");
        data.put("vehicle", JudgeUtil.in(po.getCar(), 1, 2) ? "1" : "0");
        if (po.getZhimaScore() == null) {
            data.put("sesamePointer", "1");
        } else if (po.getZhimaScore() < 600) {
            data.put("sesamePointer", "1");
        } else if (po.getZhimaScore() < 650) {
            data.put("sesamePointer", "2");
        } else if (po.getZhimaScore() < 700) {
            data.put("sesamePointer", "3");
        } else {
            data.put("sesamePointer", "4");
        }
        data.put("credit", JudgeUtil.in(po.getCreditCard(), 1, 2) ? "1" : "0");
        data.put("legalPerson", JudgeUtil.in(po.getCompany(), 1) ? "1" : "0");
        data.put("city", po.getCity());
        System.out.println(data.toJSONString());
        JSONObject params = new JSONObject();
        try {
            String encryptData = AESUtil.encrypt(data.toJSONString(), "JIANGSUnj!@#1234");
            params.put("channelCode", "YUE_FU");
            params.put("userInfo", encryptData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String url = "https://crm-api.kairyun.com/api/common/addCustomer";
        String result = restTemplate.postForObject(url, params, String.class);
        log.info("[青岛极达信息技术咨询服务有限公司]推送结果:{}", result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        if (200 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[青岛极达信息技术咨询服务有限公司]推送成功:" + result));
            return new SendResult(true, "[青岛极达信息技术咨询服务有限公司]推送成功:" + result);
        } else if (-1 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[青岛极达信息技术咨询服务有限公司]推送重复:" + result));
            return new SendResult(false, "[青岛极达信息技术咨询服务有限公司]推送重复:" + result);
        } else
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[青岛极达信息技术咨询服务有限公司]推送失败:" + result));
        return new SendResult(false, "[青岛极达信息技术咨询服务有限公司]推送失败:" + result);
    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试渠道");
        po.setMobile("13561691597");
        po.setCity("青岛市");
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
        ApiSender api = new JidaInformationForQingdaoApi();
        System.out.println(api.send(po, null));
    }

}
