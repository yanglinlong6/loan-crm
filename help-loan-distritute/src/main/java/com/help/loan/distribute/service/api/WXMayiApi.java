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
 * 无锡蚂蚁速代信息科技有限公司
 */
@Component("apiSender_20125")
public class WXMayiApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(WXMayiApi.class);

    private static final String checkUrl = "https://api.mayizhudai.com/sem_oo/check_loan.html?MobilePhone=%s";

    private static final String sendUrl = "https://api.mayizhudai.com/sem/loan_do_wx.html";

    private static final String source = "zuoxinfang";

    @Autowired
    DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[无锡蚂蚁]推送异常：{},{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[无锡蚂蚁]推送异常:"+e.getMessage()));
            return new SendResult(false,"[无锡蚂蚁]推送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        JSONObject data = new JSONObject();
        String url = String.format(checkUrl, MD5Util.getMd5String(po.getMobile()));
        String result = HttpUtil.postFormForObject(url,data);
        log.info("[无锡蚂蚁]撞库结果:{}",result);
        JSONObject resultJSON =JSONUtil.toJSON(result);
        if(0 == resultJSON.getIntValue("Ret") && 101 == resultJSON.getIntValue("Code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[无锡蚂蚁]推送重复:"+result));
            return new SendResult(false,"[无锡蚂蚁]推送重复："+result);
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
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[无锡蚂蚁]推送成功:"+result));
            return new SendResult(true,"[无锡蚂蚁]推送成功："+result);
        }else if("3".equals(result)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[无锡蚂蚁]推送重复:"+result));
            return new SendResult(false,"[无锡蚂蚁]推送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[无锡蚂蚁]推送失败:"+result));
        return new SendResult(false,"[无锡蚂蚁]推送失败："+result);
    }

    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("1ee9d91682984cdcab13ff8b90d93b06");
        po.setName("陆月");
        po.setMobile("13861834802");
        po.setCity("无锡市");
        po.setLoanAmount("5-10万");
        po.setCompany(0);
        po.setPublicFund("有，公积金月缴300-800");
        po.setCar(1);
        po.setHouse(0);
        po.setInsurance(0);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(35);
        po.setGender(1);
        po.setChannel("ttt-moerlong-lyg-jv33");
        po.setUpdateDate(new Date());
        ApiSender api = new WXMayiApi();
        System.out.println(api.send(po,null));
    }

}
