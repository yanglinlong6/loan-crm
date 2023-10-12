package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.model.CustomerPO;
import com.help.loan.distribute.service.api.utils.DESUtil;
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

/**
 * 北京惠融网
 */
@Component("apiSender_20136")
public class BJHuirongwangApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(BJHuirongwangApi.class);
    
    private static final String URL = "http://39.107.86.1/crm/import/customer";

    private static String KEY = "f1d9b83387364977b2ee63845f1dee95";

    private static Long channelId = 140L;

    private static Long channelId_360 = 140L;

    private static String key_360 = "f1d9b83387364977b2ee63845f1dee95";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京惠融网]推送未知异常："+e.getMessage()));
            return new SendResult(false,"[北京惠融网]推送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){

        Long channel = JudgeUtil.startWith(po.getChannel(),"360")?channelId_360:channelId;
        String key = JudgeUtil.startWith(po.getChannel(),"360")?key_360:KEY;

        CustomerPO customerPO = new CustomerPO();
        customerPO.setName(po.getName()+(JudgeUtil.in(po.getType(), DistributeConstant.LoanType.HOUSE)?Heng+"房抵":""));
        customerPO.setMobile(po.getMobile());
        customerPO.setChannel(channel);
        customerPO.setMedia(getMedia(po.getChannel()));
        customerPO.setCity(po.getCity());
        customerPO.setAge(po.getAge());
        customerPO.setSex(po.getGender().byteValue());
        customerPO.setNeed(LoanAmountUtil.transform(po.getLoanAmount()).toString());
        if(po.getPublicFund().contains("有，"))
            customerPO.setField2("有");
        else customerPO.setField2("无");

        if(JudgeUtil.in(po.getHouse(),1,2)){
            customerPO.setField3("有");
        }else customerPO.setField3("无");

        if(JudgeUtil.in(po.getCar(),1,2)){
            customerPO.setField4("有");
        }else customerPO.setField4("无");

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            customerPO.setField5("有");
        }else  customerPO.setField5("无");

        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            customerPO.setField6("有");
            customerPO.setField9("有");
        }else {
            customerPO.setField6("无");
            customerPO.setField9("无");
        }

        if(JudgeUtil.in(po.getCompany(),1)){
            customerPO.setField7("有");
        }else customerPO.setField7("无");
        customerPO.setRemark(getContent(po));
        String content = DESUtil.encrypt(key,customerPO.toString());
        JSONObject data = new JSONObject();
        data.put("channelId",channel);
        data.put("data",content);
        String result = HttpUtil.postForJSON(URL,data);
        JSONObject json = JSONUtil.toJSON(result);
        LOG.info("[北京惠融网]推送结果：{}",result);
        int code = json.getIntValue("code");
        if(200 == code){
            if(!JudgeUtil.startWith(po.getChannel(),"360")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京惠融网]推送成功："+result));
            }
            return new SendResult(true,"[北京惠融网]推送成功："+result);
        }else if(601 == code){
            if(!JudgeUtil.startWith(po.getChannel(),"360")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京惠融网]推送重复："+result));
            }
            return new SendResult(false,"[北京惠融网]推送重复："+result);
        }
        if(!JudgeUtil.startWith(po.getChannel(),"360")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京惠融网]推送失败："+result));
        }
        return new SendResult(false,"[北京惠融网]推送失败："+result);
    }


//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("伍测试请勿联系1");
//        po.setMobile("13632965536");
//        po.setCity("北京市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setCompany(1);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setChannel("moerlong-ttt-1022");
//        po.setOccupation(1);
//        po.setGender(1);
//        po.setCreditCard(0);
//        po.setAge(25);
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJHuirongwangApi();
//        System.out.println(api.send(po,null));
//    }
}
