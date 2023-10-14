package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.AESUtil;
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
 *  北京暖心融通金融服务外包有限公司 20137
 */
@Component("apiSender_20137")
public class NuanxinRongtongFinancialForBeijingApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(NuanxinRongtongFinancialForBeijingApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResultForJinan(po, select);
        } catch (Exception e) {
            log.error("[北京暖心融通金融服务外包有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京暖心融通金融服务外包有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[北京暖心融通金融服务外包有限公司]推送异常:" + e.getMessage());
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
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name", po.getName());
        data.put("sex", po.getGender());
        data.put("age", po.getAge());
        data.put("phone", po.getMobile());
        data.put("city", po.getCity());
        data.put("quota", po.getLoanAmount());
        // 婚姻状态未知
        data.put("marriage", 2);
        data.put("salary", "10000元");
        data.put("is_security", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? 1 : 2);
        data.put("is_provident_fund", JudgeUtil.contain(po.getPublicFund(), "无", "没有") ? 2 : 1);
        data.put("is_credit_card", JudgeUtil.in(po.getCreditCard(), 1, 2) ? 1 : 2);
        data.put("real_estate", JudgeUtil.in(po.getHouse(), 1, 2) ? 1 : 2);
        data.put("car_production", JudgeUtil.in(po.getCar(), 1) ? 1 : 2);
        data.put("insurance", JudgeUtil.in(po.getInsurance(), 1, 2) ? 1 : 2);
        System.out.println(data.toJSONString());


        JSONObject params = new JSONObject();
        try {
            String encryptData = AESUtil.encrypt(data.toJSONString(), "a74ad0ce27bfd4d6");
            params.put("data", encryptData);
            params.put("channelId", 40);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String url = "http://jixianghonglin.com/api/pushData";
        String result = restTemplate.postForObject(url, params, String.class);
        log.info("[北京暖心融通金融服务外包有限公司]推送结果:{}", result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        // code：1推送成功  0推送失败 -1系统异常
        if (1 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[北京暖心融通金融服务外包有限公司]推送成功:" + result));
            return new SendResult(true, "[北京暖心融通金融服务外包有限公司]推送成功:" + result);
        } else if (-1 == code) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[北京暖心融通金融服务外包有限公司]推送重复:" + result));
            return new SendResult(false, "[北京暖心融通金融服务外包有限公司]推送重复:" + result);
        } else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京暖心融通金融服务外包有限公司]推送失败:" + result));
        }
        return new SendResult(false, "[北京暖心融通金融服务外包有限公司]推送失败:" + result);
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
        ApiSender api = new NuanxinRongtongFinancialForBeijingApi();
        System.out.println(api.send(po, null));
    }

}
