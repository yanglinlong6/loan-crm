package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.AESUtil;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
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
 *  大连博盈
 */
@Component("apiSender_20120")
public class DLBoyingApi implements ApiSender{

    private static final Logger log = LoggerFactory.getLogger(DLBoyingApi.class);

    private static final String sendUrl = "https://miniprogram-api.zhongkongdaikuan.com/home/advertCommonSave?source=ZXFDL230204&city=dalian_zxf";

    private static final String channelCode = "kuaidai888";

    private static final String KEY = "R1dFlatmwk2vnOcI";

    private static final String IV = "CNXuNJ386jM489py";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            String city = po.getCity();
            if("大连市".equals(city)){
                return sendResultForDalian(po,select);
            }
            if("烟台市".equals(city)){
                return sendResultForYantai(po,select);
            }
            if("徐州市".equals(city)){
                return sendResultForXuzhou(po,select);
            }
            if("沈阳市".equals(city)){
                return sendResultForShengyang(po,select);
            }
            return new SendResult(false,"[大连博盈]推送异常:城市不符合:"+city);
        }catch (Exception e){
            log.error("[大连博盈]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[大连博盈]推送异常:"+e.getMessage()));
            return new SendResult(false,"[大连博盈]推送异常:"+e.getMessage());
        }

    }

    private SendResult sendResultForShengyang(UserAptitudePO po,UserDTO select) throws Exception {
        String key = "gH3YBt0vL3n8zAG0";
        String iv = "WQprMnhCRlHejs5V";
        int timestamp = DateUtil.to10();
        isHaveAptitude(po);
        String encryptMobile = AESUtil.encrypt(po.getMobile(),key,iv);
        String str = channelCode+encryptMobile+timestamp+key;
        String sign = MD5Util.getMd5String(str).toLowerCase();
        String name = AESUtil.encrypt(po.getName(),key,iv);
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
        String url = "https://miniprogram-api.zhongkongdaikuan.com/home/advertCommonSave?source=source230411&city=sy_zxf";
        String result = restTemplate.postForObject(url,data,String.class);
        log.info("[大连博盈-沈阳]推送结果:{}",result);
        JSONObject sendJSON = JSONUtil.toJSON(result);
        int code = sendJSON.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[大连博盈-沈阳]推送成功:"+result));
            return new SendResult(true,"[大连博盈-沈阳]推送成功:"+result);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[大连博盈-沈阳]推送重复:"+result));
            return new SendResult(false,"[大连博盈-沈阳]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[大连博盈-沈阳]推送失败:"+result));
        return new SendResult(false,"[大连博盈-沈阳]推送失败:");
    }

    /**
     * 徐州
     * @param po UserAptitudePO
     * @param select UserDTO
     * @return SendResult
     * @throws Exception
     */
    private SendResult sendResultForXuzhou(UserAptitudePO po,UserDTO select) throws Exception {
        String key = "gH3YBt0vL3n8zAG0";
        String iv = "WQprMnhCRlHejs5V";
        int timestamp = DateUtil.to10();
        isHaveAptitude(po);
        String encryptMobile = AESUtil.encrypt(po.getMobile(),key,iv);
        String str = channelCode+encryptMobile+timestamp+key;
        String sign = MD5Util.getMd5String(str).toLowerCase();
        String name = AESUtil.encrypt(po.getName(),key,iv);
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
        String url = "https://miniprogram-api.zhongkongdaikuan.com/home/advertCommonSave?source=source230316&city=xz_zxf";
        String result = restTemplate.postForObject(url,data,String.class);
        log.info("[大连博盈-徐州]推送结果:{}",result);
        JSONObject sendJSON = JSONUtil.toJSON(result);
        int code = sendJSON.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[大连博盈-徐州]推送成功:"+result));
            return new SendResult(true,"[大连博盈-徐州]推送成功:"+result);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[大连博盈-徐州]推送重复:"+result));
            return new SendResult(false,"[大连博盈-徐州]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[大连博盈-徐州]推送失败:"+result));
        return new SendResult(false,"[大连博盈-徐州]推送失败:");
    }

    private SendResult sendResultForYantai(UserAptitudePO po,UserDTO select) throws Exception {
        String key = "AQoI3PHKrgBjAxLp";
        String iv = "isgUxSvcrvnhmgER";
        int timestamp = DateUtil.to10();
        isHaveAptitude(po);
        String encryptMobile = AESUtil.encrypt(po.getMobile(),key,iv);
        String str = channelCode+encryptMobile+timestamp+key;
        String sign = MD5Util.getMd5String(str).toLowerCase();
        String name = AESUtil.encrypt(po.getName(),key,iv);
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
        String url = "https://miniprogram-api.zhongkongdaikuan.com/home/advertCommonSave?source=ZXFDL230204&city=yantai_zxf";
        String result = restTemplate.postForObject(url,data,String.class);
        log.info("[大连博盈-烟台]推送结果:{}",result);
        JSONObject sendJSON = JSONUtil.toJSON(result);
        int code = sendJSON.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[大连博盈-烟台]推送成功:"+result));
            return new SendResult(true,"[大连博盈-烟台]推送成功:"+result);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[大连博盈-烟台]推送重复:"+result));
            return new SendResult(false,"[大连博盈-烟台]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[大连博盈-烟台]推送失败:"+result));
        return new SendResult(false,"[大连博盈-烟台]推送失败:");
    }


    private SendResult sendResultForDalian(UserAptitudePO po, UserDTO select) throws Exception {
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/form-data");

        int timestamp = DateUtil.to10();
        isHaveAptitude(po);
        String encryptMobile = AESUtil.encrypt(po.getMobile(), KEY, IV);
        String str = channelCode+encryptMobile+timestamp+ KEY;
        String sign = MD5Util.getMd5String(str).toLowerCase();
        String name = AESUtil.encrypt(po.getName(), KEY, IV);
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
        String result = restTemplate.postForObject(sendUrl,data,String.class);
        log.info("[大连博盈]推送结果:{}",result);
        JSONObject sendJSON = JSONUtil.toJSON(result);
        int code = sendJSON.getIntValue("code");
        if(1 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[大连博盈]推送成功:"+result));
            return new SendResult(true,"[大连博盈]推送成功:"+result);
        }
        if(2 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[大连博盈]推送重复:"+result));
            return new SendResult(false,"[大连博盈]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[大连博盈]推送失败:"+result));
        return new SendResult(false,"[大连博盈]推送失败:");
    }

//    public static void main(String[] args){
////        String str = "{\"age\":34,\"car\":0,\"carPrice\":0.0,\"channel\":\"ttt-zxf-credit-bj-z9\",\"city\":\"烟台市\",\"company\":0,\"createBy\":\"web\",\"createDate\":1655111247000,\"creditCard\":0,\"gender\":1,\"getwayIncome\":1,\"house\":0,\"houseExtension\":0,\"houseState\":0,\"id\":304483,\"insurance\":0,\"isRepeat\":false,\"level\":7,\"loanAmount\":\"200000\",\"mobile\":\"19520057190\",\"name\":\"郑亮\",\"occupation\":0,\"overdue\":\"\",\"province\":\"\",\"publicFund\":\"没有公积金\",\"type\":0,\"updateBy\":\"\",\"updateDate\":1655111247000,\"userId\":\"470bf73df9c9454caccc619e83d89369\",\"weight\":0.0086994249843273,\"zhima\":0,\"zhimaScore\":0}";
////        UserAptitudePO po = JSONUtil.toJavaBean(str, UserAptitudePO.class);
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("测试请忽略5");
//        po.setMobile("13049692805");
//        po.setCity("沈阳市");
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
//        ApiSender api = new DLBoyingApi();
//        System.out.println(api.send(po,null));
//    }

}
