package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上海璨耀商务咨询有限公司
 */
@Component("apiSender_10091")
public class SHCuicanApi implements  ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHCuicanApi.class);
    private static final String checkUrl = "http://api.jianshangyidai.com/api/open/check_repeat_mobile";
    private static final String sendUrl = "http://api.jianshangyidai.com/api/open/add_user_info";

    private static final String channelKey = "908970";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【上海璀璨】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海璀璨】分发异常:"+e.getMessage()));
            return new SendResult(false,"【上海璀璨】分发异常："+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject checkData = new JSONObject();
        String md5 = MD5Util.getMd5String(MD5Util.getMd5String(po.getMobile())+"jianshang");
        checkData.put("channel_key",channelKey);
        checkData.put("encryption_mobile",md5);
        checkData.put("time",System.currentTimeMillis());

        String checkResult = HttpUtil.postForJSON(checkUrl,checkData);
        log.info("【上海璀璨】验证重复结果：{}",checkResult);
        if(200 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海璀璨】分发异常:"+checkResult));
            return new SendResult(false,"【上海璀璨】验证重复:"+checkResult);
        }

        JSONObject data = new JSONObject();
        data.put("channel_key",channelKey);
        data.put("name",po.getName());
        data.put("age",po.getAge());
        data.put("sex",(po.getGender() == null || po.getGender() !=1)?2:1);
        data.put("mobile",po.getMobile());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("loan_amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("houses", JudgeUtil.in(po.getHouse(),1,2)?2:3);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?2:3);
        data.put("life_policy",JudgeUtil.in(po.getInsurance(),1,2)?1:2);
        data.put("epf_time",po.getPublicFund().contains("有，")?2:1);
        data.put("social_security",po.getPublicFund().contains("有，")?2:1);
        if(po.getCompany() == 1)
            data.put("occupation",1);
        else
            data.put("occupation",3);
        if(po.getGetwayIncome() ==1)
            data.put("income_range",2);
        else if ((po.getGetwayIncome() == 2))
            data.put("income_range",1);
        else  data.put("income_range",1);
        data.put("reg_time", DateUtil.to10());
        data.put("tax",2);


        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【上海城达睦信】分发结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String msg = json.getString("msg");
        if(200 == json.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海璀璨】分发成功:"+result));
            return new SendResult(true,"【上海璀璨】分发成功："+msg);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海璀璨】分发失败:"+result));
        return new SendResult(false,"【上海璀璨】分发失败："+msg);
    }


//        public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("刘永军");
//        po.setMobile("15988199456");
//        po.setCity("上海市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        SHCuicanApi api = new SHCuicanApi();
//        System.out.println(api.send(po,null));
//    }
}
