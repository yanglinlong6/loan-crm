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

/*
 *  山东聚鑫助代信息管理咨询有限公司
 */
@Component("apiSender_20127")
public class SDJunxinApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(SDJunxinApi.class);

    private static final String CHECK ="http://47.94.254.100:8181/home/checkRepeatCustomer?company=1&mobile_md5=%s";
    
    private static final String SEND ="http://47.94.254.100:8181/home/adCommonSave?company=1&channel=%s&channel_account=%s";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[山东聚鑫]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[山东聚鑫]推送异常:"+e.getMessage()));
            return new SendResult(false,"[山东聚鑫]推送异常:"+e.getMessage());
        }

    }

    /**
     * 淄博
     * @param po
     * @param select
     * @return
     */
    private SendResult sendResult(UserAptitudePO po,UserDTO select){

        String checkURL = String.format(CHECK,MD5Util.getMd5String(po.getMobile()));
        String checkResult= HttpClientProxy.doGet(checkURL,new JSONObject());
        log.info("[山东聚鑫]撞库结果:{}",checkResult);
        if(JSONUtil.toJSON(checkResult).getIntValue("code") != 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[山东聚鑫]推送重复:"+checkResult));
            return new SendResult(false,"[山东聚鑫]撞库重复:"+checkResult);
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
        data.put("issued",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        String url;
        if(JudgeUtil.contain(po.getChannel(),"ttt")){
            url = String.format(SEND,9,19);
        }else if(JudgeUtil.contain(po.getChannel(),"gdt")){
            url = String.format(SEND,11,21);
        }else
            url = String.format(SEND,12,22);
        String result = restTemplate.postForObject(url,data,String.class);//
        log.info("[山东聚鑫]推送结果:{}",result);
        JSONObject jsonObject = JSONUtil.toJSON(result);
        int code = jsonObject.getIntValue("code");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[山东聚鑫]推送成功:"+result));
            return  new SendResult(true,"[山东聚鑫]推送成功:"+result);
        }else if(-1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[山东聚鑫]推送重复:"+result));
            return  new SendResult(false,"[山东聚鑫]推送重复:"+result);
        }else
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[山东聚鑫]推送失败:"+result));
        return  new SendResult(false,"[山东聚鑫]推送失败:"+result);
    }



//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试渠道");
//        po.setMobile("13561697591");
//        po.setCity("济宁市");
//        po.setLoanAmount("5-10万");
//        po.setCompany(1);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(34);
//        po.setGender(2);
//        po.setChannel("ttt-moerlong-jn-jv60");
//        po.setUpdateDate(new Date());
//        ApiSender api = new SDJunxinApi();
//        System.out.println(api.send(po,null));
//    }

}
