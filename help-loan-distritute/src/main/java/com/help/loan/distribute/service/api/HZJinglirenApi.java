package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
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
 * 金理人
 */
@Component("apiSender_20108")
public class HZJinglirenApi implements ApiSender {
    private static final Logger log = LoggerFactory.getLogger(HZJinglirenApi.class);
    private static final String sendUrl = "http://webcrmapi.xiangge.ltd/api/apply";
    private static final String fromUrl = "T1";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[金理人]推送异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[金理人]推送异常:"+e.getMessage()));
            return new SendResult(false,"[金理人]推送异常："+e.getMessage());
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("phone",Long.valueOf(po.getMobile()));
        data.put("account", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("fromUrl",fromUrl);

        // {"statusCode":200,"id":820525,"message":"申请已经提交成功,后台审核通过之后我们会尽快联系您!"}
        String result = HttpUtil.postFormForObject(sendUrl,data);
        log.info("[金理人]推送结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        int statusCode = json.getIntValue("statusCode");
        if(200 == statusCode){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[金理人]推送成功:"+result));
            return new SendResult(true,"[金理人]推送成功："+result);
        }else if(300 == statusCode){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[金理人]推送重复:"+result));
            return new SendResult(false,"[金理人]推送重复："+result);
        }else{
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[金理人]推送失败:"+result));
            return new SendResult(false,"[金理人]推送失败："+result);
        }
    }


//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍散人");
//        po.setMobile("15988189461");
//        po.setCity("杭州市");
//        po.setLoanAmount("500000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        HZJinglirenApi api = new HZJinglirenApi();
//        System.out.println(api.send(po,null));
//    }


}
