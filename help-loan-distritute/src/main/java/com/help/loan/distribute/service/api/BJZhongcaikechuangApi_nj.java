package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
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
 * 中财科创（北京）金融服务外包有限公司
 */
@Component("apiSender_10100")
public class BJZhongcaikechuangApi_nj implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(BJZhongcaikechuangApi_nj.class);

    private static final String checkUrl = "http://api.rongdians.com//V1/Check?mobile=";

    private static final String sendUrl = "http://api.rongdians.com//v1/customer";

    private static final String token = "924516E36987E3A0F147E41B46E3FE32";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【中财科创-成都】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【中财科创-成都】分发异常:"+e.getMessage()));
            return new SendResult(false,"【中财科创-成都】分发异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String md5Mobile = MD5Util.getMd5String(po.getMobile());
        String checkResult = HttpUtil.getForObject(checkUrl+md5Mobile);
        log.info("【北京中财科创】去重验证结果：{}",checkResult);
        if(!"0".equals(JSONUtil.toJSON(checkResult).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【中财科创-成都】分发重复:"+checkResult));
            return new SendResult(false,"【中财科创-成都】重复："+checkResult);
        }

        JSONObject data = new JSONObject();
        data.put("token",token);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("city",po.getCity());
        data.put("sex",(po.getGender() == null || po.getGender() !=1)?"女":"男");
        data.put("age",po.getAge());
        data.put("loanMoney", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("loanTerm","12期");
        data.put("education","未知");
        if(po.getCompany() == 1){
            data.put("job","企业主");
        }else
            data.put("job","上班");

        data.put("workYears","一年内");
        data.put("annualIncome","50万以内");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("payMethod","打卡");
            data.put("wage","5000以上");
        }else{
            data.put("payMethod","现金");
            data.put("wage","3000元以内");
        }
        if(po.getCompany() == 1){
            data.put("companyType","私企");
        }else
            data.put("companyType","未知");
        data.put("providentFund",po.getPublicFund().contains("有，")?"本地有":"无");
        data.put("socialSecurity","无");
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
//        data.put("insuranceDesc",token);
        data.put("house",JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
//        data.put("houseDesc",token);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?"有":"无");
//        data.put("carDesc",token);
        data.put("creditSituation","未知");
        data.put("microLoan","未知");
        data.put("zhimaPoints","未知");
        data.put("creditCard","未知");

        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【中财科创-成都】分发结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if("0".equals(json.getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【中财科创-成都】分发成功:"+checkResult));
            return new SendResult(true,"【中财科创-成都】分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【中财科创-成都】失败:"+checkResult));
        return new SendResult(false,"【中财科创-成都】分发失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("刘永军");
//        po.setMobile("15988199459");
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
//        BJZhongcaikechuangApi api = new BJZhongcaikechuangApi();
//        System.out.println(api.send(po,null));
//    }
}
