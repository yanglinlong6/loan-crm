package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
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
 * 赢信网:apiSender_10017
 */
@Component("apiSender_10017")
public class SHYingXinWangApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHYingXinWangApi.class);

    private static final String sendUrl = "http://101.133.148.225:8080/yxw-crm-service/outer-api/customer/receive";

    private static final String source = "fxsk";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[赢信网]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[赢信网]分发异常:"+e.getMessage()));
            return new SendResult(false,"[赢信网]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phoneNo",po.getMobile());
        data.put("quota", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("cityName",po.getCity());
        data.put("age",po.getAge());
        data.put("gender",(po.getGender()==null || po.getGender() ==0)?"未知":po.getGender());
        data.put("starLevel",0);
        data.put("status","notAccepted");
        String remark = getInfo(po);
        data.put("remark",remark);
        data.put("qualification",remark);
        data.put("source",source);
        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("[赢信网]分发结果:{}",result);
        String code = JSONUtil.toJSON(result).getString("code");
        if("200".equals(code)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[赢信网]分发成功:"+result));
            return new SendResult(true,"[赢信网]分发成功:"+result);
        }else if("601".equals(code)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[赢信网]分发失败:"+result));
            return new SendResult(false,"[赢信网]分发重复:"+result);
        }else{
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[赢信网]分发失败:"+result));
            return new SendResult(false,"[赢信网]分发失败:"+result);
        }
    }

    private java.lang.String getInfo(UserAptitudePO po){
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

    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("测试");
        po.setMobile("13049692800");
        po.setCity("上海市");
        po.setLoanAmount("50000");
        po.setCompany(0);
        po.setPublicFund("没有公积金");
        po.setCar(1);
        po.setHouse(0);
        po.setInsurance(1);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setUpdateDate(new Date());
        ApiSender api = new SHYingXinWangApi();
        System.out.println(api.send(po,null));
    }
}
