package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 杭州雨叶
 */
public class HZYuyeApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(HZYuyeApi.class);

    private static final String checkUrl = "http://zhexiangdai.com/api/customer/check_mobile";

    private static final String sendUrl = "http://zhexiangdai.com/api/customer/import_cdbg";




    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【杭州雨叶】发送异常：{}",e.getMessage(),e);
            return new SendResult(false,"【杭州雨叶】发送异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po,UserDTO select){

        String md5 = Md5Util.encryptMd5(po.getMobile());
        JSONObject data = new JSONObject();
        data.put("mobile",md5);

        String checkResult = HttpUtil.postForJSON(checkUrl,data);
        int code = JSONUtil.toJSON(checkResult).getIntValue("code");
        if(0 != code){
            return new SendResult(false,"【杭州雨叶】重复："+checkResult);
        }

        isHaveAptitude(po);
        data.clear();
        data.put("file_id",7);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",po.getCompany() == 1?1:0);
        data.put("is_credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));

        String result = HttpUtil.postForJSON(sendUrl,data);
        code = JSONUtil.toJSON(result).getIntValue("code");
        if(code == 0){
            return new SendResult(true,"【杭州雨叶】分发成功："+result);
        }
        return new SendResult(false,"【杭州雨叶】分发失败："+result);
    }


    public static void main(String[] args){

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("3f031229fbc3492ab507ad30fabe73a8");
        po.setName("宇测试2");
        po.setMobile("18262627279");
        po.setCity("上海市");
        po.setLoanAmount("300000");
        po.setCar(1);
        po.setHouse(1);
        po.setCompany(0);
        po.setPublicFund("有，个人月缴300-800元");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setUpdateDate(new Date());
        HZYuyeApi api = new HZYuyeApi ();
        System.out.println(api.send(po,null));
    }

}
