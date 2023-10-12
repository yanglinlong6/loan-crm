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
 *  兰州兴岱
 */
@Component("apiSender_20096")
public class LZXingdaiApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(LZXingdaiApi.class);

    private static final String checkUrl="http://47.94.150.166:8181/home/checkRepeatCustomer?company=2&mobile_md5=%s";
    
    private static final String sendUrl="http://47.94.150.166:8181/home/adCommonSave?company=2&channel=23&channel_account=26&uuid=xdw-api-7";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[兰州兴岱]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[兰州兴岱]分发异常:"+e.getMessage()));
            return new SendResult(false,"[兰州兴岱]分发异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po,UserDTO select){

        String url = String.format(checkUrl,MD5Util.getMd5String(po.getMobile()));
        //{"code":0,"msg":"success","data":[]}
        String checkResult=HttpUtil.postForJSON(checkUrl,new JSONObject());
        log.info("[兰州兴岱]撞库结果:{}",checkResult);
        if(JSONUtil.toJSON(checkResult).getIntValue("code") != 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[兰州兴岱]分发重复:"+checkResult));
            return new SendResult(false,"[兰州兴岱]撞库重复:"+checkResult);
        }

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name",po.getName()+Heng+parseAccount(po.getChannel()));
        data.put("mobile",po.getMobile());
        data.put("sex",po.getGender());
        data.put("loan_limit", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("room", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("accumulation",JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("social",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("policy",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("business",1);
            data.put("enterprise",1);
        }else{
            data.put("business",0);
            data.put("enterprise",0);
        }
        data.put("business",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("issued",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);

        String result = restTemplate.postForObject(sendUrl,data,String.class);//
        log.info("[兰州兴岱]分发结果:{}",result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[兰州兴岱]推送成功:"+result));
            return  new SendResult(true,"[兰州兴岱]推送成功:"+result);
        }else if(-1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[兰州兴岱]推送重复:"+result));
            return  new SendResult(false,"[兰州兴岱]推送重复:"+result);
        }else
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[兰州兴岱]推送失败:"+result));
        return  new SendResult(false,"[兰州兴岱]推送失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试请忽略");
//        po.setMobile("13049692802");
//        po.setCity("兰州市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("moerlong-ttt-30");
//        po.setUpdateDate(new Date());
//        ApiSender api = new LZXingdaiApi();
//        System.out.println(api.send(po,null));
//    }

}
