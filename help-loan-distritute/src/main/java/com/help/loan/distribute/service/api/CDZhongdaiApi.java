package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.UnicodeUtil;
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
 * 杭州银贷
 */
@Component("apiSender_20103")
public class CDZhongdaiApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(CDZhongdaiApi.class);

    private static final String checkUrl = "http://112.126.75.7:8500/customers/check/MD5Encrypt?phone_encrypt=%s&company_id=%s";

    private static final String sendUrl = "http://112.126.75.7:8500/customers";

    private static final String companyId = "dfb4f69a-0bab-4c74-9293-541bbc87ace3";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[成都众代]推送异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[成都众代]推送未知异常："+e.getMessage()));
            return new SendResult(false,"[成都众代]推送异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws InterruptedException {

        String url = String.format(checkUrl,MD5Util.getMd5String(po.getMobile()),companyId);
        String result = HttpUtil.postFormData(url,new JSONObject());
        log.info("[成都众代]撞库结果：{}",result);
        if(!"0".equals(JSONUtil.toJSON(result).getString("data"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[成都众代]客户已存在："+result));
            return new SendResult(false,"[成都众代]客户已存在："+result);
        }
        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("gender",(null == po.getGender() || 1 != po.getGender())?"Female":"Male");
        data.put("age",po.getAge()<25?28:po.getAge());
        data.put("grade","Grade0");
        data.put("limit", Integer.valueOf(LoanAmountUtil.transform(po.getLoanAmount())));
        if(1 == po.getGetwayIncome()){
            data.put("income_year",8000*12);
        }else if(2==po.getGetwayIncome()){
            data.put("income_year",5000*12);
        }else data.put("income_year",0);
        data.put("customer_remark",getContent(po));
        data.put("is_married",false);
        data.put("provider_code","1002");
        data.put("company_id",companyId);
        result = UnicodeUtil.toCN(HttpUtil.postForJSON(sendUrl,data));
        log.info("[成都众代]推送结果:{}",result);
        if(JudgeUtil.contain(result,"成功")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[成都众代]推送成功:"+result));
            Thread.sleep(2000);
            return new SendResult(true,"[成都众代]发送成功:"+result);
        }
        //{"code":"1","msg":"号码已存在"}
        if(JSONUtil.toJSON(result).getString("msg").equals("has exist")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[成都众代]推送重复:"+result));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[成都众代]推送失败:"+result));
        return new SendResult(false,"[成都众代]推送失败:"+result);
    }

    private String getInfo2(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("Businesslicense").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("AccumulationFund").append(",");
        }

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("SalaryAfterTax").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("House").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("Car").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("Policy").append(",");
        }
        String content = memo.toString();
        if(content.endsWith(",")){
            content = content.substring(0,content.length()-1);
        }
        return content;
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试1");
//        po.setMobile("13632965541");
//        po.setCity("成都市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(32);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CDZhongdaiApi();
//        System.out.println(api.send(po,null));
//    }


}
