package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  北京泽安讯代信息科技有限公司 20138
 */
@Component("apiSender_20138")
public class ZeAnXundaiInformationForBeijingApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(ZeAnXundaiInformationForBeijingApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[北京泽安讯代信息科技有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京泽安讯代信息科技有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[北京泽安讯代信息科技有限公司]推送异常:" + e.getMessage());
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
        checkData.put("MobilePhone", MD5Util.getMd5String(po.getMobile()));
        String checkResult = HttpUtil.postFormForObject("https://api.shanjiezhongxin.com/sem_oo/check_loan.html", checkData);
        log.info("[北京泽安讯代信息科技有限公司]撞库结果:{}", checkResult);
        if (JSONUtil.toJSON(checkResult).getIntValue("Code") != 100) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[北京泽安讯代信息科技有限公司]推送重复:" + checkResult));
            return new SendResult(false, "[北京泽安讯代信息科技有限公司]撞库重复:" + checkResult);
        }
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        data.put("city", StringUtils.isEmpty(po.getCity()) ? "" : po.getCity().replace("市", ""));
        data.put("car", JudgeUtil.in(po.getCar(), 1) ? "有" : "无");
        data.put("age", po.getAge());
        data.put("job", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "有" : "无");
        data.put("house", JudgeUtil.in(po.getHouse(), 1, 2) ? "有" : "无");
        data.put("baodan_is", JudgeUtil.in(po.getInsurance(), 1, 2) ? "有" : "无");
        data.put("sex", po.getGender() == 1 ? "男" : "女");
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        // 2023/10/13 固定值，具体需要和对接人确定
        data.put("source", "qdyuefu");
        data.put("shebao", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "有" : "无");
        data.put("gongjijin", JudgeUtil.in(po.getPublicFund(), "有,") ? "有" : "无");
        data.put("isbankpay", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "是" : "否");
        data.put("check_num", "");
        data.put("ip", "127.0.0.1");
        data.put("credit_card", JudgeUtil.in(po.getCreditCard(), 1, 2) ? "有" : "无");
        if (JudgeUtil.contain(po.getChannel(), "ttt")) {
            data.put("meiti", "头条");
        } else {
            data.put("meiti", "朋友圈");
        }
        data.put("time", DateUtil.to10());
        data.put("weili", 0);
        System.out.println(data.toJSONString());

        String url = "https://api.shanjiezhongxin.com/sem/loan_do.html";
        String result = HttpUtil.postFormForObject(url, data);
        log.info("[北京泽安讯代信息科技有限公司]推送结果:{}", result);
        if (Long.parseLong(result) > 1000) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[北京泽安讯代信息科技有限公司]推送成功:" + result));
            return new SendResult(true, "[北京泽安讯代信息科技有限公司]推送成功：" + result);
        } else if ("3".equals(result)) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[北京泽安讯代信息科技有限公司]推送重复:" + result));
            return new SendResult(false, "[北京泽安讯代信息科技有限公司]推送重复：" + result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京泽安讯代信息科技有限公司]推送失败:" + result));
        return new SendResult(false, "[北京泽安讯代信息科技有限公司]推送失败：" + result);
    }

    public static void main(String[] args) {
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试渠道");
//        po.setMobile("13561691597");
////        po.setCity("济南市");
//        po.setCity("北京市");
//        po.setLoanAmount("20");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(34);
//        po.setGender(2);
//        po.setChannel("ttt-zxf-jn-jv60");
//        po.setUpdateDate(new Date());

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试请忽略");
        po.setMobile("13561691600");
        po.setCity("北京市");
        po.setLoanAmount("500000");
        po.setCompany(1);
        po.setPublicFund("有，公积金月缴300-800");
        po.setCar(1);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(1);
        po.setOccupation(1);
        po.setCreditCard(1);
        po.setAge(30);
        po.setGender(1);
        po.setChannel("zxf-ttt");
        po.setUpdateDate(new Date());
        ApiSender api = new ZeAnXundaiInformationForBeijingApi();
        System.out.println(api.send(po, null));
    }
}
