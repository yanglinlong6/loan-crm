package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.CustomerPO;
import com.help.loan.distribute.common.utils.*;
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

/*
 *  北京鑫岱
 */
@Component("apiSender_20089")
public class BJXinDaiIIApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(BJXinDaiIIApi.class);

    private static final String checkUrl = "https://miniprogram-api.chizicrm.com/home/commonCheckMobile?company=1&city=beijing_zbwy"
            +"&channel_code=%s&mobile=%s&sign=%s&timestamp=%s";

    private static final String sendUrl = "https://miniprogram-api.chizicrm.com/home/advertCommonSave?source=source20220612&city=beijing_zbwy";

//            "&channel_code=%s" +
//            "&mobile=%s" +
//            "&sign=%s" +
//            "&timestamp=%s" +
//            "&name=%s"+
//            "&sex=%s"+
//            "&loan_limit=%s"+
//            "&room=%s"+
//            "&car=%s"+
//            "&accumulation=%s"+
//            "&social=%s"+
//            "&policy=%s"+
//            "&business=%s"+
//            "&issued=%s"+
//            "&enterprise=%s";

    private static final String channelCode = "kuaidai888";

    private static final String key = "WCLqXnOB9fHLySYr";

    private static final String iv = "hpoXf7v05Hzchp9T";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            log.error("[北京鑫岱]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京鑫岱]分发异常:"+e.getMessage()));
            return new SendResult(false,"[北京鑫岱]分发异常:"+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po,UserDTO select) throws Exception {
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/form-data");

        int timestamp = DateUtil.to10();
        String mobile = po.getMobile().substring(0,8);
        String encryptMobile = AESUtil.encrypt(mobile,key,iv);
        String sign = MD5Util.getMd5String(channelCode+encryptMobile+timestamp+key).toLowerCase();
        String url = String.format(checkUrl,channelCode,encryptMobile,sign,timestamp);
        String result = HttpClientProxy.doPost(url, new JSONObject(), "UTF-8", 3000, httpHeader);
        log.info("[北京鑫岱]撞库结果:{}",result);
        JSONObject checkJSON = JSONUtil.toJSON(result);
        int code = checkJSON.getIntValue("code");
        if(-1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京鑫岱]撞库失败:"+result));
            return new SendResult(false,"[北京鑫岱]撞库失败:"+result);
        }
        JSONArray array = checkJSON.getJSONArray("data");
        if(1 == code && JudgeUtil.in(MD5Util.getMd5String(po.getMobile()),checkJSON.getJSONArray("data").toArray(new String[array.size()]))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京鑫岱]撞库重复:"+result));
            return new SendResult(false,"[北京鑫岱]撞库重复:"+result);
        }

        isHaveAptitude(po);

        mobile = po.getMobile();
        encryptMobile = AESUtil.encrypt(mobile,key,iv);
        String str = channelCode+encryptMobile+timestamp+key;
        sign = MD5Util.getMd5String(str).toLowerCase();

        String name = AESUtil.encrypt(po.getName(),key,iv);
//        url = String.format(sendUrl,channelCode,encryptMobile,sign,timestamp,name,po.getGender(),LoanAmountUtil.transform(po.getLoanAmount()),po.getHouse(),po.getCar(),
//                JudgeUtil.contain(po.getPublicFund(),"有，"),0,po.getInsurance(),po.getCompany(),po.getGetwayIncome(),0);
        JSONObject data = new JSONObject();
        data.put("channel_code",channelCode);
        data.put("mobile",encryptMobile);
        data.put("sign",sign);
        data.put("timestamp",timestamp);
        data.put("name",name);
        data.put("sex",po.getGender());
        data.put("loan_limit",LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("room",po.getHouse());
        data.put("car",po.getCar());
        data.put("accumulation",JudgeUtil.contain(po.getPublicFund(),"有，")?1:0);
        data.put("social",0);
        data.put("policy",po.getInsurance());
        data.put("business",po.getCompany());
        data.put("issued",po.getGetwayIncome());
        data.put("enterprise",0);

        System.out.println(data.toJSONString());
//        result = HttpUtilForJava.doPost(sendUrl,data,httpHeader);

//        result = HttpClientProxy.doPostWithFormDate(sendUrl,"application/form-data",data);
        result = restTemplate.postForObject(sendUrl,data,String.class);
        log.info("[北京鑫岱]推送结果:{}",result);
        JSONObject sendJSON = JSONUtil.toJSON(result);
        code = sendJSON.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[北京鑫岱]推送成功:"+result));
            return new SendResult(true,"[北京鑫岱]推送成功:"+result);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[北京鑫岱]推送重复:"+result));
            return new SendResult(false,"[北京鑫岱]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[北京鑫岱]推送失败:"+result));
        return new SendResult(false,"[北京鑫岱]推送失败:");
    }

    public static void main(String[] args){
        String str = "{\"age\":34,\"car\":0,\"carPrice\":0.0,\"channel\":\"ttt-zxf-credit-bj-z9\",\"city\":\"北京市\",\"company\":0,\"createBy\":\"web\",\"createDate\":1655111247000,\"creditCard\":0,\"gender\":1,\"getwayIncome\":1,\"house\":0,\"houseExtension\":0,\"houseState\":0,\"id\":304483,\"insurance\":0,\"isRepeat\":false,\"level\":7,\"loanAmount\":\"200000\",\"mobile\":\"19520057189\",\"name\":\"郑亮\",\"occupation\":0,\"overdue\":\"\",\"province\":\"\",\"publicFund\":\"没有公积金\",\"type\":0,\"updateBy\":\"\",\"updateDate\":1655111247000,\"userId\":\"470bf73df9c9454caccc619e83d89369\",\"weight\":0.0086994249843273,\"zhima\":0,\"zhimaScore\":0}";
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试请忽略3");
//        po.setMobile("13049692803");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
        UserAptitudePO po = JSONUtil.toJavaBean(str, UserAptitudePO.class);
        ApiSender api = new BJXinDaiIIApi();
        System.out.println(api.send(po,null));
    }

}
