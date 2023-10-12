package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
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

/**
 * 杭州浙民贷
 */
@Component("apiSender_20092")
public class HZZheMinDaiApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(HZZheMinDaiApi.class);

    private static final String CHECK_URL = "http://dk.zmnxn.com/newcustomer/preCheck";

    private static final String URL = "http://dk.zmnxn.com/newcustomer/get";

//    private static final String MODE = "username=%s&mobile=%s&address=%s&sex=%s&money_req=%s&salary=%s&accumulation=%s&house=%s&car=%s&insurance=%s&enterprise_invoicing=%s";

    @Autowired
    CacheService cacheService;

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[杭州浙民贷]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【杭州浙民贷】分发失败："+e.getMessage()));
            return new SendResult(false,"[杭州浙民贷]推送异常:"+e.getMessage());
        }
    }
    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        JSONObject data = new JSONObject();
        data.put("mobile_md5", MD5Util.getMd5String(po.getMobile()));
        String response = HttpClientProxy.doPost(CHECK_URL, data, "UTF-8", 3000, httpHeader);
        LOG.info("[杭州浙民贷]撞库结果:{}",response);
        if(!"1".equals(response)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【杭州浙民贷】推送重复："+response));
            return new SendResult(false,"[杭州浙民贷]推送重复:"+response);
        }

        isHaveAptitude(po);

        String username = po.getName()+"-"+parseAccount(po.getChannel());
        int salary = JudgeUtil.in(po.getGetwayIncome(),1,2)?2:1;
        int accumulation = po.getPublicFund().contains("有，")?2:1;
        int house = JudgeUtil.in(po.getHouse(),1,2)?2:1;
        int car = JudgeUtil.in(po.getCar(),1,2)?2:1;
        String insurance = JudgeUtil.in(po.getInsurance(),1,2)?"Y":"N";
        int enterprise_invoicing = 1;

        data.clear();
        data.put("username",username);
        data.put("mobile",po.getMobile());
        data.put("address",po.getCity());
        data.put("sex",po.getGender());
        data.put("money_req",LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("salary",salary);
        data.put("accumulation",accumulation);
        data.put("house",house);
        data.put("car",car);
        data.put("insurance",insurance);
        data.put("enterprise_invoicing",enterprise_invoicing);
        data.put("source",24);

        response = HttpUtil.postForJSON(URL,data);

        LOG.info("[杭州浙民贷]推送结果:{}",response);
        if("1".equals(response)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【杭州浙民贷】分发成功："+response));
            return new SendResult(true,"[杭州浙民贷]推送成功:"+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【杭州浙民贷】分发失败："+response));
        return new SendResult(false,"[杭州浙民贷]推送失败:"+response);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("伍散人测试请忽略56");
//        po.setMobile("13652965156");
//        po.setCity("杭州市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setAge(33);
//        po.setGender(1);
//        po.setChannel("weibo-0012");
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//
//        ApiSender apiSender = new HZZheMinDaiApi();
//        apiSender.send(po,null);
//
//    }
}
