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

import java.util.*;

/**
 * 上海越柠
 */
//@Component("apiSender_10017")
public class SHYueLingAPI implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(SHYueLingAPI.class);

    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
//            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海越柠】:"+e.getMessage()));
            return new SendResult(false,"【上海越柠】分发失败");
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        return send3(po,select);
    }

    private SendResult send3(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject obj  = new JSONObject();
        obj.put("name", po.getName());
        obj.put("cityName", po.getCity());
        obj.put("phoneNo", po.getMobile());
        obj.put("age", (null == po.getAge() || po.getAge() <=0)?30:po.getAge());
        obj.put("quota",LoanAmountUtil.transform(po.getLoanAmount()));
        obj.put("gender", (null == po.getGender() || JudgeUtil.in(po.getGetwayIncome(),0,2)) ?2:1);
        StringBuilder sb = new StringBuilder();
        if(po.getPublicFund().contains("有，")) {
        	sb.append("有公积金,");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)) {
        	sb.append("有保单,");
        }
        if(JudgeUtil.in(po.getCompany(),1)) {
        	sb.append("有营业执照,");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)) {
        	sb.append("有商品房,");
        }
        if(JudgeUtil.in(po.getCar(),1,2)) {
        	sb.append("有车,");
        }
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)) {
        	sb.append("有代发工资");
        }
        obj.put("qualification", sb.toString());
        obj.put("source", "FX");
        String result = HttpUtil.postForJSON("http://101.133.148.225:8080/yxw-crm-service/outer-api/customer/receive", obj.toJSONString());
        if(JSONUtil.isJsonString(result) && "200".equals(JSONUtil.toJSON(result).getString("code"))){
//            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
            return new SendResult(true,result);
        }else
//            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
        return new SendResult(false,result);
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
        po.setCar(0);
        po.setHouse(0);
        po.setInsurance(1);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setUpdateDate(new Date());
        ApiSender api = new SHYueLingAPI();
        System.out.println(api.send(po,null));
    }
}
