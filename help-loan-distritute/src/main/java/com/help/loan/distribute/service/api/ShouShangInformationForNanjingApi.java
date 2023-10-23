package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
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
 *  南京首商信息科技有限公司  20142
 */
@Component("apiSender_20142")
public class ShouShangInformationForNanjingApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(ShouShangInformationForNanjingApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[武汉启楚商务咨询有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[武汉启楚商务咨询有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[武汉启楚商务咨询有限公司]推送异常:" + e.getMessage());
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
        checkData.put("mobile", MD5Util.getMd5String(po.getMobile()));
        String checkURL = "http://47.96.87.61/api/customer/check_mobile";
        String checkResult = HttpUtil.postForJSON(checkURL, checkData);
        log.info("[武汉启楚商务咨询有限公司]撞库结果:{}", checkResult);
        if (JSONUtil.toJSON(checkResult).getIntValue("code") != 0) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[武汉启楚商务咨询有限公司]推送重复:" + checkResult));
            return new SendResult(false, "[武汉启楚商务咨询有限公司]撞库重复:" + checkResult);
        }

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("file_id", 22);
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        data.put("age", po.getAge());
        data.put("sex", po.getGender());
        data.put("city", po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(), 1, 2) ? 1 : 0);
        data.put("is_car", JudgeUtil.in(po.getCar(), 1, 2) ? 1 : 0);
        data.put("is_company", po.getCompany() == 1 ? 1 : 0);
        data.put("is_credit", JudgeUtil.in(po.getCreditCard(), 1, 2) ? 1 : 0);
        data.put("is_insurance", JudgeUtil.in(po.getInsurance(), 1, 2) ? 1 : 0);
        data.put("is_social", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? 1 : 0);
        data.put("is_fund", po.getPublicFund().contains("有，") ? 1 : 0);
        data.put("is_work", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? 1 : 0);
        data.put("is_tax", JudgeUtil.in(po.getCompany(), 1) ? 1 : 0);
        data.put("webank", 0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));
        System.out.println(data.toJSONString());
        String url = "http://47.96.87.61/api/customer/import_data";
        String result = restTemplate.postForObject(url, data, String.class);
        log.info("[武汉启楚商务咨询有限公司]推送结果:{}", result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        if (0 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[武汉启楚商务咨询有限公司]推送成功:" + result));
            return new SendResult(true, "[武汉启楚商务咨询有限公司]推送成功:" + result);
        } else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[武汉启楚商务咨询有限公司]推送失败:" + result));
        }
        return new SendResult(false, "[武汉启楚商务咨询有限公司]推送失败:" + result);
    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试渠道");
        po.setMobile("13561691598");
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
        ApiSender api = new ShouShangInformationForNanjingApi();
        System.out.println(api.send(po, null));
    }

}
