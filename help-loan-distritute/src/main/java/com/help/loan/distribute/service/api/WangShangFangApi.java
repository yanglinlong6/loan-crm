package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DisConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 上海努作金融信息服务有限公司：上海，杭州：房抵
 */
@Component("apiSender_20060")
public class WangShangFangApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(WangShangFangApi.class);

    private static final String checkUrl = "http://dbz.huidaikeji.com/api/api/collidingxd?mobile=";

    private static final String sendUrl = "http://dbz.huidaikeji.com/api/api/add2";

    private static final String checkUrlSuzhou = "http://dbz.huidaikeji.com/api/api/collidingcrm?mobile=%s&department_id=%s";

    private static final String sendUrlSuzhou = "http://dbz.huidaikeji.com/api/api/addcrm";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            String type = po.getCreateBy();
            if(StringUtils.isNotBlank(type) && JudgeUtil.in(type, DisConstant.User.Type.HOUSE)){
                if(StringUtils.isNotBlank(po.getCity()) && "苏州市".equals(po.getCity())){
                    return sendSuZhou(po,select);
                }
                return send2(po,select);
            }
            return new SendResult(false,"【网商-房抵】非房抵客户");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【网商-房抵】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【网商-房抵】发送异常："+e.getMessage());
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        String checkResult = HttpUtil.getForObject(checkUrl+ MD5Util.getMd5String(po.getMobile()));
        log.info("【网商-房抵】验证手机号码结果：{}",checkResult);
        //{"code":200,"message":"请求成功"}
        if(200 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【网商-房抵】分发重复："+checkResult));
            return new SendResult(false,"【网商-房抵】验证手机号码:重复："+checkResult);
        }
        JSONObject data = new JSONObject();
        data.put("code","xdhcfdtg02");
        data.put("flag","tt");
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",(po.getAge()==null || po.getAge() <= 0 )?30:po.getAge());
        data.put("sex",(po.getGender() == null || po.getGender() != 1)?0:1);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund",po.getPublicFund().contains("有，")?1:0);
        data.put("social",0);
        data.put("credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("webank",0);
        data.put("tax",0);
        data.put("work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("city",po.getCity());

        String encrypt = AESUtil.java_openssl_encrypt(data.toJSONString(),"DbzTJ2txzXiaoLia").toLowerCase();
        data.clear();
        data.put("data",encrypt);
        String result  = HttpUtil.postForJSON(sendUrl,data);
        log.info("【网商-房抵】分发结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if(500 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【网商-房抵】分发成功"+result));
            return new SendResult(true,"【网商-房抵】:"+result);
        }
        String msg = resultJSON.getString("msg");
        if("电话号码重复".equals(msg)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【网商-房抵】分发重复"+result));
            return new SendResult(false,"【网商-房抵】"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【网商-房抵】分发失败："+result));
        return new SendResult(false,"【网商-房抵】分发失败"+result);
    }



    private static final long department_id = 899;
    private SendResult sendSuZhou(UserAptitudePO po, UserDTO select) throws Exception {

        String url = String.format(checkUrlSuzhou,MD5Util.getMd5String(po.getMobile()),department_id);
        String checkResult = HttpUtil.getForObject(url);
        log.info("【网商-房抵-苏州】验证手机号码结果：{}",checkResult);
        //{"code":200,"message":"请求成功"}
        if(200 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【网商-房抵-苏州】分发重复："+checkResult));
            return new SendResult(false,"【网商-房抵-苏州】验证手机号码:重复："+checkResult);
        }
        JSONObject data = new JSONObject();
        data.put("code","xdhcfdtg02");
        data.put("flag","tt");
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",(po.getAge()==null || po.getAge() <= 0 )?30:po.getAge());
        data.put("sex",(po.getGender() == null || po.getGender() != 1)?0:1);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund",po.getPublicFund().contains("有，")?1:0);
        data.put("social",0);
        data.put("credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("webank",0);
        data.put("tax",0);
        data.put("work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("city",po.getCity());
        data.put("department_id",department_id);

        String encrypt = AESUtil.java_openssl_encrypt(data.toJSONString(),"DbzTJ2txzXiaoLia").toLowerCase();
        data.clear();
        data.put("data",encrypt);
        String result  = HttpUtil.postForJSON(sendUrlSuzhou,data);
        log.info("【网商-房抵】分发结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if(500 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【网商-房抵-苏州】分发成功"+result));
            return new SendResult(true,"【网商-房抵-苏州】:"+result);
        }
        String msg = resultJSON.getString("msg");
        if("电话号码重复".equals(msg)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【网商-房抵-苏州】分发重复"+result));
            return new SendResult(false,"【网商-房抵-苏州】"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【网商-房抵-苏州】分发失败："+result));
        return new SendResult(false,"【网商-房抵-苏州】分发失败"+result);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("a93eb2c587c44b6a806445d47b3dcfd0");
//        po.setName("头条测试");
//        po.setHouse(1);
//        po.setCity("苏州市");
//        po.setCompany(1);
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13761172137");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setAge(33);
//        po.setChannel("ttt001");
//        po.setCreateBy("house");
//        po.setUpdateDate(new Date());
//        WangShangFangApi api = new WangShangFangApi();
//        System.out.println(api.send(po,null));
//    }
}
