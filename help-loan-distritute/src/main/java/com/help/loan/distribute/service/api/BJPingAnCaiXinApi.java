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
 * 北京平安鑫财
 */
@Component("apiSender_20013")
public class BJPingAnCaiXinApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJPingAnCaiXinApi.class);

    private static final String checkUrl = "http://112.126.75.7:8700/customers?encrypt_phone=";

    private static final String sendUrl = "http://112.126.75.7:8700/customers";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【平安鑫财-北京】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【平安财鑫-北京】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【平安鑫财-北京】分发异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        String url = checkUrl+ MD5Util.getMd5String(po.getMobile());
        String result = HttpUtil.getForObject(url);
        log.info("【平安鑫财-北京】撞库结果：{}",result);
        if(!"0".equals(JSONUtil.toJSON(result).getString("data"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【平安鑫财-北京】分发未知异常："+result));
            return new SendResult(false,"【平安鑫财-北京】客户已存在："+result);
        }

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("gender",(null == po.getGender() || 1 != po.getGender())?"Female":"Male");
        data.put("age",po.getAge()<25?28:po.getAge());
        data.put("grade","Grade0");
        data.put("limit", Integer.valueOf(LoanAmountUtil.transform(po.getLoanAmount())));
        if(1 == po.getGetwayIncome()){
            data.put("annual_income",8000*12);
        }else if(2==po.getGetwayIncome()){
            data.put("annual_income",5000*12);
        }else data.put("annual_income",0);
        data.put("remark_condition","");
        data.put("is_married",false);
        data.put("source_from","邦正");
        data.put("mortgages",getInfo2(po));
        data.put("education","");
        data.put("profession","");
        result=HttpUtil.postForJSON(sendUrl,data);
        log.info("[平安鑫财-北京]分发结果:{}",result);
        String code = JSONUtil.toJSON(result).getString("code");
        if(code.equals("0")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[平安鑫财-北京]分发成功:"+result));
            return new SendResult(true,"[平安鑫财-北京]发送成功:"+result);
        }
        //{"code":"1","msg":"号码已存在"}
        if(JSONUtil.toJSON(result).getString("msg").equals("has exist")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[平安鑫财-北京]分发重复:"+result));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[平安鑫财-北京]分发失败:"+result));
        return new SendResult(false,"[平安鑫财-北京]发送失败:"+result);
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

    private java.lang.String getInfo2(UserAptitudePO po){
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
//        po.setName("伍先森测试33");
//        po.setMobile("13632965536");
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
//        ApiSender api = new BJPingAnCaiXinApi();
//        System.out.println(api.send(po,null));
//    }


}
