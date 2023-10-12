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

import java.util.Date;

/**
 * 国鑫通融（北京）金融服务外包有限公司
 */
@Component("apiSender_20054")
public class BJGuoXinApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(BJGuoXinApi.class);

    private static final String checkUrl = "http://open-api.zhongdaijinrong.com/openApi/old/checkPhone";

    private static final String sendUrl = "http://open-api.zhongdaijinrong.com/openApi/old/insertUser";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【北京国鑫】提送异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【国鑫通融（北京）】提送异常:"+e.getMessage()));
            return new SendResult(false,"【国鑫通融（北京）】提送异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){


        JSONObject data = new JSONObject();
        data.put("phone",po.getMobile());
        long time = System.currentTimeMillis();
        String sign = MD5Util.getMd5String(new StringBuffer().append("timestamp=").append(time).append("&").append("phone=").append(po.getMobile()).toString());
        data.put("timestamp",time);
        data.put("sign",sign);
        String result = HttpUtil.postForJSON(checkUrl,data);
        log.info("【国鑫通融（北京）】提送结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if( 0 == json.getIntValue("code") && json.getJSONObject("data").getIntValue("biz_code") == 200){
            return register(po,select,time,sign);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【国鑫通融（北京）】手机号已存在"));
        return new SendResult(false,"【国鑫通融（北京）】提送重复："+result);
    }

    private SendResult register(UserAptitudePO po, UserDTO select,long time,String sign){

        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("phone",po.getMobile());
        data.put("timestamp",time);
        data.put("sign",sign);
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("name", po.getName());
        data.put("qixian","");
        if(po.getGetwayIncome() == 1){
            data.put("dkMoney",8000);
        }else if(po.getGetwayIncome() == 2){
            data.put("dkMoney",5000);
        }else {
            data.put("dkMoney",0);
        }
        data.put("gjjsbState", po.getPublicFund().contains("有，")?0:1);
        data.put("guaranteeSlipState",JudgeUtil.in(po.getInsurance(),1,2)?0:1);
        data.put("monthlyRoomState",JudgeUtil.in(po.getCar(),1,2)?0:1);
        data.put("fullRoomState",JudgeUtil.in(po.getHouse(),1,2)?0:1);
        data.put("weilidai",1);
        data.put("source","bz");
        String result = HttpUtil.postForJSON(sendUrl,data);
        log.info("【北京国鑫通融】提送结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        if(0 == json.getIntValue("code") && "成功".equals(json.getString("msg"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【国鑫通融（北京）】提送成功："+result));
            return new SendResult(true,"【国鑫通融（北京）】提送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【国鑫通融（北京）】提送失败"+result));
        return new SendResult(false,"【国鑫通融（北京）】提送失败："+result);
    }

    
//	public static void main(String[] args) {
//		UserAptitudePO po = new UserAptitudePO();
//		po.setUserId(null);
//		po.setName("伍散人测试请忽略2");
//		po.setMobile("13649692803");
//		po.setCity("北京市");
//		po.setLoanAmount("50000");
//		po.setCompany(1);
//		po.setPublicFund("有，公积金月缴300-800");
//		po.setCar(1);
//		po.setHouse(1);
//		po.setInsurance(1);
//		po.setGetwayIncome(1);
//		po.setOccupation(1);
//		po.setCreditCard(1);
//		po.setAge(30);
//		po.setGender(1);
//		po.setUpdateDate(new Date());
//		ApiSender api = new BJGuoXinApi();
//		System.out.println(api.send(po, null));
//	}
}
