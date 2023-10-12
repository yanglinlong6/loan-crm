package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/*
 *  郑州快亿融-济南
 */
@Component("apiSender_20122")
public class ZZKuaiyirongForJinanApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(ZZKuaiyirongForJinanApi.class);
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResultForJinan(po,select);
        }catch (Exception e){
            log.error("[`郑州快亿融]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[郑州快亿融]推送异常:"+e.getMessage()));
            return new SendResult(false,"[郑州快亿融]推送异常:"+e.getMessage());
        }

    }

    /**
     * 济南
     * @param po
     * @param select
     * @return
     */
    private SendResult sendResultForJinan(UserAptitudePO po,UserDTO select){

        String checkURL = String.format("http://api-crm.krdcrm.cn:8181/home/checkRepeatCustomer?company=1&mobile_md5=%s",MD5Util.getMd5String(po.getMobile()));
        String checkResult= HttpClientProxy.doGet(checkURL,new JSONObject());
        log.info("[郑州快亿融-济南]撞库结果:{}",checkResult);
        if(JSONUtil.toJSON(checkResult).getIntValue("code") != 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[郑州快亿融-济南]推送重复:"+checkResult));
            return new SendResult(false,"[郑州快亿融-济南]撞库重复:"+checkResult);
        }

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name",po.getName()+Heng+parseAccount(po.getChannel()));
        data.put("mobile",po.getMobile());
        data.put("sex",po.getGender());
        data.put("loan_limit", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("room", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("accumulation",JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("social",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("policy",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        if(JudgeUtil.in(po.getCompany(),1)){
            data.put("business",1);
            data.put("enterprise",1);
        }else{
            data.put("business",0);
            data.put("enterprise",0);
        }
        data.put("business",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("issued",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        System.out.println(data.toJSONString());
        String url = "http://api-crm.krdcrm.cn:8181/home/adCommonSave?company=1&channel=14&channel_account=14&uuid=jn-api-5";
        String result = restTemplate.postForObject(url,data,String.class);//
        log.info("[郑州快亿融-济南]推送结果:{}",result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[郑州快亿融-济南]推送成功:"+result));
            return  new SendResult(true,"[郑州快亿融-济南]推送成功:"+result);
        }else if(-1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[郑州快亿融-济南]推送重复:"+result));
            return  new SendResult(false,"[郑州快亿融-济南]推送重复:"+result);
        }else
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[郑州快亿融-济南]推送失败:"+result));
        return  new SendResult(false,"[郑州快亿融-济南]推送失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试渠道");
//        po.setMobile("13561691597");
//        po.setCity("济南市");
//        po.setLoanAmount("5-10万");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(34);
//        po.setGender(2);
//        po.setChannel("ttt-zxf-jn-jv60");
//        po.setUpdateDate(new Date());
//        ApiSender api = new ZZKuaiyirongForJinanApi();
//        System.out.println(api.send(po,null));
//    }

}
