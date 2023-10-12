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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 北京亿拓:北京亿拓信息技术有限公司?
 */
@Component("apiSender_20055")
public class BJYiTuoApi implements ApiSender{

    private static final Logger log  = LoggerFactory.getLogger(BJYiTuoApi.class);

    private static final String checkUrl = "http://wx.baigwang.com/public/JOLygSkfIQ.php/customer/collide_table?pid=%s&cid=%s";

    private static final  int cid = 22;

    private static final String key = "022";

    private static final String sendUrl = "https://wx.baigwang.com/public/JOLygSkfIQ.php/customer/getinfo";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            if(StringUtils.isNotBlank(po.getChannel()) && po.getChannel().toLowerCase().contains("ttt")){
                return sendResult(po,select);
            }
            return  new SendResult(false,"【北京亿拓】只接收头条客户");
        }catch (Exception e){
            log.error("[北京亿拓]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿拓]分发异常:"+e.getMessage()));
            return new SendResult(false,"[北京亿拓]分发异常:"+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);
        String pid = MD5Util.getMd5String(po.getMobile());
        String url = String.format(checkUrl,pid,cid);

        String result = HttpUtil.getForObject(url);
        log.info("[北京亿拓]撞库验证结果:{}",result);
        int stats = JSONUtil.toJSON(result).getIntValue("stats");
        if(2 != stats){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京亿拓]分发重复:"+result));
            return new SendResult(false,"[北京亿拓]分发重复:"+result);
        }
        JSONObject data = new JSONObject();
        data.put("cid",cid);
        data.put("source_two",cid+"01");
        data.put("key",key);
        data.put("username",po.getName());
        data.put("userphone",po.getMobile());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("loan_amount", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        data.put("houses", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("life_policy",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("epf_time",po.getPublicFund().contains("有，")?1:0);
        data.put("social_security",0);
        data.put("particle_loan",0);
        data.put("credit_card",0);
        data.put("income_range",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("age",po.getAge());
        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("identity",2);
            data.put("business_license",1);
        }else{
            data.put("business_license",0);
        }
        String sendResult = HttpUtil.postForJSON(sendUrl,data);
        log.info("[北京亿拓]发送结果:{}",sendResult);
        stats = JSONUtil.toJSON(sendResult).getIntValue("stats");
        if(1 == stats){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京亿拓]分发成功:"+result));
            return new SendResult(true,"[北京亿拓]分发成功:"+sendResult);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京亿拓]分发失败:"+result));
        return  new SendResult(false,"[北京亿拓]分发失败:"+sendResult);
    }

//        public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人测试请忽略");
//        po.setMobile("15988199463");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCompany(1);
//        po.setPublicFund("有，");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BeiJinYiTuoApi();
//        System.out.println(api.send(po,null));
//    }

}
