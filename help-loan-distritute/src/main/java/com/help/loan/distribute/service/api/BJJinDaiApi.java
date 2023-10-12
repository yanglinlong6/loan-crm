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

import java.util.Date;


/**
 * 北京金贷: apiSender_20032
 */
@Component("apiSender_20032")
public class BJJinDaiApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJJinDaiApi.class);

    private static final String checkUrl = "http://121.36.92.202:8600/customers?encrypt_phone=";

    private static final String sendUrl = "http://121.36.92.202:8600/customers";


    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京金贷]分发异常:{}",e.getMessage(),e);
            return new SendResult(false,"[北京金贷]分发异常:"+e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);
        //{ "data": "0", "code": 0, "msg": "ok"}
        String checkResult= HttpUtil.getForObject(checkUrl+MD5Util.getMd5String(po.getMobile()));
        log.info("[北京金贷]撞库结果:{}",checkResult);
        if(!JSONUtil.toJSON(checkResult).getString("data").equals("0")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京金贷]分发重复:"+checkResult));
            return new SendResult(false,"[北京金贷]撞库重复:"+checkResult);
        }
        JSONObject data = new JSONObject();
        data.clear();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("gender",(null == po.getGender() || 1 != po.getGender() )?"Female":"Male");
        data.put("age",po.getAge());
        data.put("grade","");
        data.put("limit",LoanAmountUtil.transform(po.getLoanAmount()));
        if(1 == po.getGetwayIncome()){
            data.put("annual_income",8000*12);
        }else if(2==po.getGetwayIncome()){
            data.put("annual_income",5000*12);
        }else data.put("annual_income",0);
        data.put("remark_condition",getInfo(po));
        data.put("is_married",false);
        data.put("source_from","邦正");
        data.put("mortgages",getInfo2(po));
        data.put("education","");
        data.put("profession","");
        String result=HttpUtil.postForJSON(sendUrl,data);
        log.info("[北京金贷]分发结果:{}",result);
        String code = JSONUtil.toJSON(result).getString("code");
        if(code.equals("0")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京金贷]分发成功:"+result));
            return new SendResult(true,"[北京金贷]发送成功:"+result);
        }
        //{"code":"1","msg":"号码已存在"}
        if(JSONUtil.toJSON(result).getString("msg").equals("has exist")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京金贷]分发重复:"+result));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京金贷]分发失败:"+result));
        return new SendResult(false,"[北京金贷]发送失败:"+result);
    }

    private String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有营业执照").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("，");
        }

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发工资").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有商品房").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业寿险保单").append(",");
        }
        memo.append("申请金额：").append(po.getLoanAmount());
        return memo.toString();
    }

    private String getInfo2(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("Businesslicense").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("AccumulationFund").append("，");
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
//        po.setName("伍散人测试5");
//        po.setMobile("13632965535");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJJinDaiApi();
//        System.out.println(api.send(po,null));
//    }
}
