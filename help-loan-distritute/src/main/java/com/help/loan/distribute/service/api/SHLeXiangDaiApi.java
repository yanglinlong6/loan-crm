package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtilForJava;
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
 * 乐享贷
 */
@Component("apiSender_20012")
public class SHLeXiangDaiApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SHLeXiangDaiApi.class);

    private static final String url = "https://rms.zhonghdk.com/api/Save.ashx";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            log.error("【乐享贷-上海】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【乐享贷-上海】分发未知异常："+e.getMessage()));
            return new SendResult(false,"");
        }

    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("campaign",1311);
        data.put("puk","F4EDB980C967293007E94179160CEE98");
        data.put("from","bangzheng:"+po.getCity());
        data.put("ext1", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("ext2", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("ext3", po.getPublicFund().contains("有，")?"有":"无");
        data.put("ext4", Integer.valueOf(LoanAmountUtil.transform(po.getLoanAmount())));
        data.put("ext5", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("ext6", JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("ext7", "无");
        data.put("ext8", JudgeUtil.in(po.getCreditCard(),1,2)?"有":"无");
        data.put("ext9", JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("ext10", JudgeUtil.in(po.getCompany(),1)?"有":"无");

        Map<String,String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        String result = HttpClientProxy.doPost(url, data, "UTF-8", 3000, httpHeader);//{"code":1,"message":""}
        log.info("【乐享贷-上海】分发结果：{}",result);
        int code = JSONUtil.toJSON(result).getIntValue("code");
        if(1==code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【乐享贷-上海】发送成功："+result));
            return new SendResult(true,"【乐享贷-上海】发送成功："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【乐享贷-上海】发送失败："+result));
        return new SendResult(false,"【乐享贷-上海】发送失败："+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("伍先森测试");
//        po.setMobile("13632965531");
//        po.setCity("上海市");
//        po.setLoanAmount("5000000");
//        po.setCompany(0);
//        po.setPublicFund("公积金有，");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ApiSender api = new SHLeXiangDaiApi();
//        System.out.println(api.send(po,null));
//
//    }
}
