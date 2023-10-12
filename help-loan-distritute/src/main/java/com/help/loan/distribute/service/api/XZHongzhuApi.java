package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
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

import java.util.Date;

/**
 * 徐州鸿祝
 */
@Component("apiSender_20100")
public class XZHongzhuApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(XZHongzhuApi.class);

    private static final String sendUrl = "http://39.104.22.149/zderp/zdApi/index";

    private static final String SOURCE = "zxf";

    private static final String TOKEN = "0UbXbvLoNQ2B";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[徐州鸿祝]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[徐州鸿祝]推送异常:"+e.getMessage()));
            return new SendResult(false,"[徐州鸿祝]推送异常:"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("city",po.getCity());
        data.put("quota", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("qualifications",getContent(po));
        data.put("source",SOURCE);
        data.put("token",TOKEN);
        String result = HttpUtil.postForJSON(sendUrl,data);
        if("success".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[徐州鸿祝]推送成功:"+result));
            return new SendResult(true,"[徐州鸿祝]推送成功:"+result);
        }
        if(JudgeUtil.contain(result,"手机号已存在")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[徐州鸿祝]推送重复:"+result));
            return new SendResult(false,"[徐州鸿祝]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[徐州鸿祝]推送失败:"+result));
        return new SendResult(false,"[徐州鸿祝]推送失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试请忽略1");
//        po.setMobile("13632965530");
//        po.setCity("济宁市");
//        po.setLoanAmount("500000");
//        po.setCompany(1);
//        po.setPublicFund("有，公积金月缴300-800");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("zxf-ttt");
//        po.setUpdateDate(new Date());
//        ApiSender api = new XZHongzhuApi();
//        System.out.println(api.send(po,null));
//    }
}
