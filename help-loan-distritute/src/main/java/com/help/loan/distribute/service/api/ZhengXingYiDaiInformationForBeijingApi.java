package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/*
 * 	北京正行易代信息科技有限公司 20150
 */
@Component("apiSender_20150")
public class ZhengXingYiDaiInformationForBeijingApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(ZhengXingYiDaiInformationForBeijingApi.class);

    private static final String sendUrl = "https://api.miaodaizhongxin.com/sem/loan_do_bjzz.html";

    private static final String meiti = "YFU";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po, select);
        } catch (Exception e) {
            log.error("[北京正行易代信息科技有限公司]推送异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京正行易代信息科技有限公司]推送异常:" + e.getMessage()));
            return new SendResult(false, "[北京正行易代信息科技有限公司]推送异常" + e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws UnsupportedEncodingException {

        // 撞库检查
        JSONObject data = new JSONObject();
        isHaveAptitude(po);
        data.clear();
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        data.put("city", po.getCity().endsWith("市") ? po.getCity().substring(0, po.getCity().length() - 1) : po.getCity());
        data.put("car", JudgeUtil.in(po.getCar(), 1, 2) ? "有" : "无");
        data.put("age", po.getAge());
        data.put("job", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "有" : "无");
        data.put("house", JudgeUtil.in(po.getHouse(), 1, 2) ? "有" : "无");
        data.put("baodan_is", JudgeUtil.in(po.getInsurance(), 1, 2) ? "有" : "无");
        data.put("sex", (null == po.getGender() || 2 == po.getGender()) ? "女" : "男");
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("source", meiti);
        data.put("shebao", JudgeUtil.contain(po.getPublicFund(), "有，") ? "有" : "无");
        data.put("gongjijin", JudgeUtil.contain(po.getPublicFund(), "有，") ? "有" : "无");
        data.put("isbankpay", JudgeUtil.in(po.getGetwayIncome(), 1, 2) ? "是" : "否");
        data.put("check_num", "");
        data.put("ip", "127.0.0.1");
        data.put("credit_card", "无");
        if (JudgeUtil.contain(po.getChannel(), "ttt"))
            data.put("meiti", "头条");
        else data.put("meiti", "朋友圈");
        data.put("time", DateUtil.to10());
        data.put("weili", 0);
        String result = HttpUtil.postFormForObject(sendUrl, data);
        log.info("[北京正行易代信息科技有限公司]推送结果:{}", result);//1471102
        Long code = Long.valueOf(result);
        if (code > 80000) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 1, "[北京正行易代信息科技有限公司]推送成功:" + result));
            return new SendResult(true, "[北京正行易代信息科技有限公司]推送成功:" + result);
        }
        if (3 == code.intValue()) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 0, "[北京正行易代信息科技有限公司]推送重复:" + result));
            return new SendResult(false, "[北京正行易代信息科技有限公司]推送重复:" + result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "[北京正行易代信息科技有限公司]推送失败:" + result));
        return new SendResult(false, "[北京正行易代信息科技有限公司]推送失败:" + result);

    }

    public static void main(String[] args) {
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试请忽略");
        po.setMobile("13632965522");
        po.setCity("北京市");
        po.setLoanAmount("500000");
        po.setCompany(1);
        po.setPublicFund("有，公积金月缴300-800");
        po.setCar(1);
        po.setHouse(1);
        po.setInsurance(1);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setChannel("zxf-ttt");
        po.setUpdateDate(new Date());
        ApiSender api = new ZhengXingYiDaiInformationForBeijingApi();
        System.out.println(api.send(po, null));
    }

}
