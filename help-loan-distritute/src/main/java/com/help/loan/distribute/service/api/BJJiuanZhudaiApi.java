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

import java.util.Date;

/**
 * 北京久安助贷信息科技有限公司
 */
@Component("apiSender_20123")
public class BJJiuanZhudaiApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(BJJiuanZhudaiApi.class);

    private static final String checkUrl = "https://api.xiaodaizhongxin.com/sem_oo/check_loan.html?MobilePhone=%s";

    private static final String sendUrl = "https://api.xiaodaizhongxin.com/sem/loan_do.html";

    private static final String source = "qdzxf";

    @Autowired
    DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京久安]推送异常：{},{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京久安]推送异常:"+e.getMessage()));
            return new SendResult(false,"[北京久安]推送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        JSONObject data = new JSONObject();
        String url = String.format(checkUrl, MD5Util.getMd5String(po.getMobile()));
        String result = HttpUtil.postFormForObject(url,data);
        log.info("[北京久安]撞库结果:{}",result);
        JSONObject resultJSON =JSONUtil.toJSON(result);
        if(0 == resultJSON.getIntValue("Ret") && 101 == resultJSON.getIntValue("Code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京久安]推送重复:"+result));
            return new SendResult(false,"[北京久安]推送重复："+result);
        }
        isHaveAptitude(po);
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("car", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("age",po.getAge());
        data.put("job", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("baodan_is", JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("sex",po.getGender() ==1?"男":"女");
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("source",source);
        data.put("shebao", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("gongjijin", po.getPublicFund().contains("有，")?"有":"无");
        data.put("isbankpay", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("ip","127.0.0.1");
        data.put("credit_card", JudgeUtil.in(po.getCreditCard(),1,2)?"有":"无");
        if(JudgeUtil.contain(po.getChannel(),"ttt"))
            data.put("meiti", "头条");
        else data.put("meiti", "朋友圈");
        data.put("time", DateUtil.to10());
        data.put("weili",0);
        //71568
        result = HttpUtil.postFormForObject(sendUrl,data);
        if(Long.valueOf(result).longValue() > 1000){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京久安]推送成功:"+result));
            return new SendResult(true,"[北京久安]推送成功："+result);
        }else if("3".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京久安]推送重复:"+result));
            return new SendResult(false,"[北京久安]推送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京久安]推送失败:"+result));
        return new SendResult(false,"[北京久安]推送失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试请忽略");
//        po.setMobile("13632965524");
//        po.setCity("北京市");
//        po.setLoanAmount("500000");
//        po.setCompany(1);
//        po.setPublicFund("有，公积金月缴300-800");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("zxf-ttt");
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJJiuanZhudaiApi();
//        System.out.println(api.send(po,null));
//    }

}
