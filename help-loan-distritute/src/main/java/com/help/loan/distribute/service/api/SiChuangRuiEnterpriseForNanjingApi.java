package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
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
 *  南京思创瑞企业管理有限公司  20144
 */
@Component("apiSender_20144")
public class SiChuangRuiEnterpriseForNanjingApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SiChuangRuiEnterpriseForNanjingApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[南京思创瑞企业管理有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[南京思创瑞企业管理有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[南京思创瑞企业管理有限公司]推送异常:" + e.getMessage());
        }

    }

    /**
     * 济南
     *
     * @param po
     * @param select
     * @return
     */
    private SendResult sendResultForJinan(UserAptitudePO po, UserDTO select) throws Exception {
        JSONObject checkData = new JSONObject();
        checkData.put("phone", MD5Util.getMd5String(po.getMobile()));
        String checkURL = "http://47.242.43.151/Home/EncryptApi/checkRegister";
        String checkResult = HttpUtil.postForJSON(checkURL, checkData);
        log.info("[南京思创瑞企业管理有限公司]撞库结果:{}", checkResult);
        log.info("isRegistered:{}", JSONUtil.toJSON(JSONUtil.toJSON(checkResult).get("data")).getIntValue("isRegistered"));
        if (JSONUtil.toJSON(JSONUtil.toJSON(checkResult).get("data")).getIntValue("isRegistered") != 0) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[南京思创瑞企业管理有限公司]推送重复:" + checkResult));
            return new SendResult(false, "[南京思创瑞企业管理有限公司]撞库重复:" + checkResult);
        }

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        if (JudgeUtil.contain(po.getChannel(), "ttt"))
            data.put("channel", "头条");
        else data.put("channel", "朋友圈");
        data.put("phone", po.getMobile());
        data.put("city", po.getCity());
        data.put("name", po.getName());
        data.put("age", po.getAge());
        data.put("sex", po.getGender());
        data.put("loan", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("social_security_and_accumulation_fund", JudgeUtil.contain(po.getPublicFund(), "有，") ? 1 : 2);
        data.put("life_insurance", JudgeUtil.in(po.getInsurance(), 1, 2) ? 1 : 2);
        data.put("house_property", JudgeUtil.in(po.getHouse(), 1, 2) ? 5 : 4);
        data.put("car_property", JudgeUtil.in(po.getCar(), 1, 2) ? 2 : 3);
        System.out.println(data.toJSONString());
        JSONObject params = new JSONObject();
        params.put("source", "YueFu");
        params.put("data", AESUtil.encrypt(data.toJSONString(), "hxW7zT4BI9siImpE"));
        String url = "http://47.242.43.151/Home/EncryptApi/reg";
        String result = restTemplate.postForObject(url, params, String.class);
        log.info("[南京思创瑞企业管理有限公司]推送结果:{}", result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        int status = JSONUtil.toJSON(jsonObject.get("data")).getIntValue("status");
        log.info("code==" + code + ";status==" + status);
        if (1 == status && 200 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[南京思创瑞企业管理有限公司]推送成功:" + result));
            return new SendResult(true, "[南京思创瑞企业管理有限公司]推送成功:" + result);
        } else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[南京思创瑞企业管理有限公司]推送失败:" + result));
        }
        return new SendResult(false, "[南京思创瑞企业管理有限公司]推送失败:" + result);
    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试渠道");
        po.setMobile("13561691599");
        po.setCity("南京市");
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
        ApiSender api = new SiChuangRuiEnterpriseForNanjingApi();
        System.out.println(api.send(po, null));
    }

}
