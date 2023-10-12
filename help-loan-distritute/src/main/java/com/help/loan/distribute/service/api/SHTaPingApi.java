package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 上海塔平商务咨询有限公司 apiSender_20050
 */
@Component("apiSender_20050")
public class SHTaPingApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHTaPingApi.class);

    private static final String sendUrl = "http://www.shenglidai.net/index.php/api/index/add.html";

    private static final String key = "sxrE2I00UMTH8Zbt1MLDNM0qirVE6MowYZep0UKJpnXsLefZtb";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            if(StringUtils.isNotBlank(po.getChannel()) && po.getChannel().toLowerCase().contains("ttt")){
                return sendResult(po,select);
            }
            return  new SendResult(false,"【上海塔平】只接收头条客户");
        }catch (Exception e){
            log.error("【上海塔平】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海塔平】分发异常:"+e.getMessage()));
            return new SendResult(false,"【上海塔平】分发异常："+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("key",key);
        data.put("sex",(po.getGender() == null || po.getGender() !=1)?"女":"男");
        data.put("has_house", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("has_car",JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("has_baodan",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("has_gongjijin",po.getPublicFund().contains("有，")?"有":"无");
        data.put("has_weilidai","无");
        data.put("wenlidai","无");
        data.put("has_nasui","无");
        data.put("mayi_score","无");
        data.put("has_job",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("has_credit_card",JudgeUtil.in(po.getCreditCard(),1,2)?"有":"无");
        data.put("can_phone_time", "能电话沟通时间");


        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        String result = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("【上海塔平】分发结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String msg = json.getString("errMsg");
        if(0 == json.getIntValue("errNum")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海塔平】分发成功:"+result));
            return new SendResult(true,"【上海塔平】分发成功："+msg);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海塔平】分发失败:"+result));
        return new SendResult(false,"【上海塔平】分发失败："+msg);
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("刘永军");
//        po.setMobile("15988199459");
//        po.setCity("上海市");
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
//        SHTaPingApi api = new SHTaPingApi();
//        System.out.println(api.send(po,null));
//    }







}
