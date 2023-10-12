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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 众特斯：apiSender_10048
 */
@Component("apiSender_10048")
public class ZhongTeApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(ZhongTeApi.class);

    private static final String appid = "TD20";

    private static final String appSecret = "usIjWr0pNkv4RQYxqS";

    private static final String url = "http://www.sztedai.com/api/outside/customInfo";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error("【众特思】分发异常:"+e.getMessage(),3);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【众特思】分发异常:"+e.getMessage()));
            return new SendResult(false,e.getMessage());
        }
    }


    private SendResult send2(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject data = new JSONObject();
        data.put("appid",appid);
        data.put("mobile",po.getMobile());
        data.put("name",po.getName());
        data.put("city",po.getCity());
        data.put("age",po.getAge() == 0?32:po.getAge());
        data.put("sex",po.getGender()==null?2:po.getGender());
        data.put("daiMoney", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("haveCredit", JudgeUtil.in(po.getCreditCard(),1,2)?2:1);
        data.put("haveInsure",JudgeUtil.in(po.getInsurance(),1,2)?2:1);
        data.put("hourseDai",JudgeUtil.in(po.getHouse(),1,2)?2:1);
        data.put("carDai",JudgeUtil.in(po.getCar(),1,2)?2:1);
        if(1==po.getOccupation().intValue())
            data.put("occapation","上班");
        else if(2 == po.getOccupation().intValue())
            data.put("occapation","做生意");
        else if(3 == po.getOccupation().intValue())
            data.put("occapation","即上班又做生意");
        else
            data.put("occapation","");

        if(1==po.getGetwayIncome().intValue()){
            data.put("payType",3);
            data.put("income","银行代发5000以上");
        }else if(2 == po.getGetwayIncome().intValue()){
            data.put("payType",3);
            data.put("income","银行代发5000以下");
        }else if(3 == po.getGetwayIncome().intValue() ) {
            data.put("payType",2);
            data.put("income","");
        }else{
            data.put("payType",1);
            data.put("income","");
        }
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("income","银行代发");
        }
        if(Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund()) || Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund()) || Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund())) {
            data.put("reservedFunds",2);
        }else
            data.put("reservedFunds",1);
        data.put("socialSecurity",1);
        data.put("daiTime","");
        data.put("dataLevel","A");
        data.put("ad","");

        List<String> keyList = new ArrayList<>(data.keySet());
        keyList.sort((key1,key2) -> key1.compareTo(key2));
        StringBuffer content = new StringBuffer();
        for (String key : keyList) {
            content.append(data.get(key));
        }
        String md5 = MD5Util.getMd5String(content.toString());
        String sign = MD5Util.getMd5String(md5+appSecret);
        data.put("sign",sign);
        String response = HttpUtil.postForJSON(url,data);
        log.info("【众特思】分发结果：{}",JSONUtil.toJSON(response).getString("errmsg"));
        if(JSONUtil.isJsonString(response)){
            JSONObject result = JSONUtil.toJSON(response);
            int code = result.getIntValue("errcode");
            String msg = result.getString("errmsg");
            if(0 == code){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【众特思】分发成功:"+msg));
                return new SendResult(true,response);
            }else if(10004 == code ){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【众特思】:"+msg));
                return new SendResult(false,"【众特思】重复："+msg);
            }else{
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【众特思】:"+msg));
                return new SendResult(false,response);
            }
        }
        return new SendResult(false,"【众特思】分发异常："+response);
    }


//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("测试张先森");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("武汉市");
//        po.setCompany(0);
//        po.setInsurance(1);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965156");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴500元以上");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setGender(1);
//        po.setUpdateDate(new Date());
//        ZhongTeApi api = new ZhongTeApi();
//        System.out.println(api.send(po,null));
//
//    }
}
