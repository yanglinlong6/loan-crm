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
 * 北京汇鑫: apiSender_10119(简贷)
 */
@Component("apiSender_10119")
public class BJHuiXinApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJHuiXinApi.class);

    private static final String checkUrl = "http://8.133.168.33:8899/api/customer/check_mobile";

    private static final String sendUrl = "http://8.133.168.33:8899/api/customer/import_tttg";

    private static final int file_id = 10;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京汇鑫]分发异常:{}",e.getMessage(),e);
            return new SendResult(false,"[北京汇鑫]分发异常:"+e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);
        JSONObject data=new JSONObject();
        data.put("mobile", MD5Util.getMd5String(po.getMobile()));
        data.put("file_id",4);
        //{"code":"0","msg":"校验通过"}
        String checkResult= HttpUtil.postForJSON(checkUrl,data);
        log.info("[北京汇鑫]撞库结果:{}",checkResult);
        if(!JSONUtil.toJSON(checkResult).getString("code").equals("0")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京汇鑫]分发重复:"+checkResult));
            return new SendResult(false,"[北京汇鑫]撞库重复:"+checkResult);
        }

        data.clear();
        data.put("file_id",file_id);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("is_house",1);
        }else data.put("is_house",0);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("is_car",1);
        }else data.put("is_car",0);

        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("is_company",1);
        }else data.put("is_company",0);

        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            data.put("is_credit",1);
        }else data.put("is_credit",0);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("is_insurance",1);
        }else data.put("is_insurance",0);
        data.put("is_social",0);
        if(po.getPublicFund().contains("有，")){
            data.put("is_fund",1);
        }else  data.put("is_fund",0);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("is_work",1);
        }else data.put("is_work",0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("c_imp_type",1);
        //{"code":"0","msg":"提交成功"}
        String result=HttpUtil.postForJSON(sendUrl,data);
        log.info("[北京汇鑫]分发结果:{}",result);
        String code = JSONUtil.toJSON(result).getString("code");
        if(code.equals("0")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京汇鑫]分发成功:"+result));
            return new SendResult(true,"[北京汇鑫]发送成功:"+result);
        }
        //{"code":"1","msg":"号码已存在"}
        if(JSONUtil.toJSON(result).getString("msg").equals("号码已存在")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京汇鑫]分发重复:"+result));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京汇鑫]分发失败:"+result));
        return new SendResult(false,"[北京汇鑫]发送失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张先森测试");
//        po.setMobile("13632965531");
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
//        ApiSender api = new BJHuiXinApi();
//        System.out.println(api.send(po,null));
//    }
}
