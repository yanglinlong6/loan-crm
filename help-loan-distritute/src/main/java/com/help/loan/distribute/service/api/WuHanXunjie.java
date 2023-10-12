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
 * 武汉迅捷:  10112
 */
@Component("apiSender_10112")
public class WuHanXunjie implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(WuHanXunjie.class);

    private static final String checkUrl = "http://47.94.84.21/api/customer/check_mobile";

    private static final String sendUrl = "http://47.94.84.21/api/customer/import_data";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[武汉迅捷]发送异常:{}",e.getMessage(),e);
            return new SendResult(false,"[武汉迅捷]发送异常:"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("mobile", MD5Util.getMd5String(po.getMobile()));
        // {"code":"0","msg":"校验通过"}
        String checkResult = HttpUtil.postForJSON(checkUrl,data);
        log.info("[武汉迅捷]撞库结果:{}",checkResult);
        if(!"0".equals(JSONUtil.toJSON(checkResult).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【武汉迅捷】分发重复:"+checkResult));
            return new SendResult(false,"[武汉迅捷]撞库结果:重复:"+checkResult);
        }
        data.clear();

        data.put("file_id",18);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car", JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",po.getCompany().intValue() == 1?1:0);
        data.put("is_credit",0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));
        // {"code":"0","msg":"提交成功"}
        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("[武汉迅捷]分发结果：{}",result);
        if("0".equals(JSONUtil.toJSON(result).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【武汉迅捷】分发成功："+result));
            return new SendResult(true,"[武汉迅捷]分发成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉迅捷】分发失败:"+result));
        return new SendResult(false,"[武汉迅捷]分发失败："+result);
    }


//        public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("刘永军测试2");
//        po.setMobile("15988199462");
//        po.setCity("武汉市");
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
//        ApiSender api = new WuHanXunjie();
//        System.out.println(api.send(po,null));
//    }

}
