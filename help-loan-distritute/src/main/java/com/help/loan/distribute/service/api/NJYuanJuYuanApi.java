package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 南京市源聚缘商务信息咨询有限公司
 */
@Component("apiSender_20064")
public class NJYuanJuYuanApi implements  ApiSender{

    private static final Logger log = LoggerFactory.getLogger(NJYuanJuYuanApi.class);
    private static final String sendUrl="http://www.njyjy.net/api/reg?cid=%s&v=%s&src=%s";
    private static final int cid=30;
    private static final String src="广点通";
    private static final String key="08f5b8786d5d88a17f0c50da5b277b1b";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;



    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[南京源聚缘]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[南京源聚缘]分发异常:"+e.getMessage()));
            return new SendResult(false,"[南京源聚缘]分发异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("age",po.getAge());
        data.put("sex",po.getGender());
        data.put("mobile",po.getMobile());
        data.put("ip","127.0.0.1");
        data.put("city",po.getCity().contains("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("loan_amount", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("houses",2);
        }else data.put("houses",3);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("car",2);
        }else data.put("car",3);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("life_policy",1);
        }else data.put("life_policy",2);

        //是否有本地公积金
        if(po.getPublicFund().contains("有，")){
            data.put("epf_time",3);
        }else data.put("epf_time",1);

        data.put("social_security",1);
        data.put("particle_loan",1);

        if(po.getCompany() == 1){
            data.put("occupation",1);
        }else if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("occupation",3);
        }else  data.put("occupation",4);

        if(po.getGetwayIncome() ==1){
            data.put("income_range",2);
        }else if(po.getGetwayIncome() == 2){
            data.put("income_range",1);
        }
        data.put("tax",2);

        String v = MD5Util.getMd5String(po.getMobile()+cid+key).toLowerCase();
        String url= String.format(sendUrl, cid,v,src);
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"errno":"5","msg":"重复提交"}
        String result = HttpClientProxy.doPost(url, data, "UTF-8", 20000, httpHeader);//{"errno":"0","msg":"OK"}
        log.info("[南京源聚缘]发送结果:{}",result);
        JSONObject json=JSONUtil.toJSON(result);
        String msg = json.getString("msg");
        String errno = json.getString("errno");
        if("0".equals(errno)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[南京源聚缘]分发成功:"+result));
            return new SendResult(true,"[南京源聚缘]分发成功:"+result);
        }else if("5".equals(errno)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[南京源聚缘]分发重复:"+result));
            return new SendResult(false,"[南京源聚缘]分发重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[南京源聚缘]分发失败:"+result));
        return new SendResult(false,"[南京源聚缘]分发失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人");
//        po.setMobile("15988199466");
//        po.setCity("南京市");
//        po.setLoanAmount("500000");
//        po.setCompany(1);
//        po.setPublicFund("有，");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new NJYuanJuYuanApi();
//        System.out.println(api.send(po,null));
//    }
}
