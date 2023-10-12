package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
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

@Component("apiSender_10039")
public class ShengLiApi implements ApiSender {

    private static Logger LOG = LoggerFactory.getLogger(ShengLiApi.class);

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    private static final String verifyUrl = "http://47.114.61.84:10002/verify";

    public static final String API_URL = "http://47.114.61.84:10002/apply";

    private static final String checking = "33d274e545be4886884107d54ee4cfba";

    private static final String channelNo = "A15";


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send1(po,select);
        }catch (Exception e){
            LOG.error("【省利】分发异常：{}，{}",po.getMobile(),e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【省利】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【省利】分发未知异常："+e.getMessage());
        }

    }


    public SendResult send1(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ciphertext", MD5Util.getMd5String(po.getMobile()).toLowerCase());
        //{"resultCode":600,"msg":"手机号重复","data":{"ciphertext":"f1f39754ea01f033ea6055f7918d218e"}}
        String verifyResult = HttpUtil.postForJSON(verifyUrl,jsonObject);
        JSONObject verifyResultJson = JSONUtil.toJSON(verifyResult);
        if (600 == verifyResultJson.getIntValue("resultCode")) {
            LOG.error("【省利】验证手机号码-{}重复",po.getMobile());
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【省利】分发："+verifyResult));
            return new SendResult(false,"【省利】验证手机号码重复");
        }

        JSONObject data = new JSONObject();
        data.put("checking",checking);
        data.put("channelNo",channelNo);
        data.put("customerName",po.getName());
        data.put("phone",po.getMobile());
        data.put("applyMoney", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("house","有");
        }else data.put("house","无");

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("carInfo","有");
        }else data.put("carInfo","无");
        if(po.getPublicFund().contains("有，")){
            data.put("commonFoud","有");
        }else
            data.put("commonFoud","无");
        data.put("socialSecurity","无");
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("scommercialInsurance","有");
        }else data.put("scommercialInsurance","无");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(data);

        String result = HttpUtil.postForJSON(API_URL,jsonArray.toJSONString());
        if(StringUtils.isNotBlank(result) && JSONUtil.isJsonString(result)){
            JSONObject resultJson = JSONUtil.toJSON(result);
            if(200 == resultJson.getIntValue("resultCode")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【省利】分发成功："+result));
                return new SendResult(true,"【省利】分发成功："+result);
            }else{
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【省利】分发失败："+result));
                return new SendResult(false,"【省利】分发失败："+result);
            }
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【省利】分发失败："+result));
        return new SendResult(false,"【省利】分发失败："+result);
    }

//    public static void main(String[] args){
//        String mobile = "18334869317";
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("ciphertext", MD5Util.getMd5String(mobile).toLowerCase());
//        String result = HttpUtil.postForObject(verifyUrl,jsonObject);
//        System.out.println(result);
//    }
}
