package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.GenderUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * 51银行贷：
 */
public class Bank51Api
        implements ApiSender {
    private static final String API_SEND_URL = "http://www.51yhdai.com/api.php";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    private static Logger logger = LoggerFactory.getLogger(Bank51Api.class);

    public SendResult send(UserAptitudePO po, UserDTO select) {
        if(org.apache.commons.lang3.StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(po.getName())) {
                if(StringUtils.isBlank(parse.get("openid").toString())) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        JSONObject params = new JSONObject();
        params.put("trafficSources", "dfkj");
        params.put("customName", po.getName());
        params.put("mobile", po.getMobile());
        if(po.getCity().endsWith("市")){
            params.put("cityname", po.getCity().substring(0,po.getCity().length()-1));
        }else
            params.put("cityname", po.getCity());

        params.put("sex", GenderUtil.transform(po.getGender()));
        params.put("age", Integer.valueOf(30));
        params.put("amount", LoanAmountUtil.transform(po.getLoanAmount()));
        params.put("loanType", "信用贷");
        String salary = "";
        if(po.getGetwayIncome().intValue() == 1) {
            salary = "5000";
        } else if(po.getGetwayIncome().intValue() == 2) {
            salary = "3000";
        }
        params.put("salary", salary);
        params.put("insurance", "2");
        int accumulationFund = 2;
        if((Pattern.matches("^\\D*\\d+-\\d+\\D*$", po.getPublicFund())) || (Pattern.matches("^\\D*\\d+\\D*$", po.getPublicFund())) || (Pattern.matches("^.*[一二三四五六七八九十百千万亿]+.*$", po.getPublicFund()))) {
            accumulationFund = 1;
        }
        params.put("accumulationFund", Integer.valueOf(accumulationFund));
        params.put("creditYears", "0");
        params.put("credit", "2");
        int house = 2;
        if((po.getHouse().intValue() == 1) || (po.getHouse().intValue() == 2)) {
            house = 1;
        }
        params.put("house", Integer.valueOf(house));
        int car = 2;
        if(po.getCar().intValue() == 1) {
            car = 1;
        }
        params.put("car", Integer.valueOf(car));
        params.put("carLoan", "无");
        params.put("houseLoan", "无");
        String insurancePolicy = "无";
        if(po.getInsurance().intValue() == 1) {
            insurancePolicy = "有半年以上保单";
        }
        params.put("insurancePolicy", insurancePolicy);
        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("applyDate", dataformat.format(po.getUpdateDate()));
        params.put("credit_score", po.getZhimaScore());
        params.put("creditQuota", "0");
        params.put("businessLicense", po.getCompany());
        params.put("column1", "0");
        params.put("column2", "无");
        JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        array.add(params);
        obj.put("thirdCustomerResult", array);
        LinkedMultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap();
        paramsMap.add("Obj", obj.toJSONString());
        String postForObject = HttpUtil.postForFormUrlencoded("http://www.51yhdai.com/api.php", paramsMap);
        logger.info("send bank51：userId-{} 发送结果：{}" ,po.getUserId(),postForObject);
        JSONObject parseObject = JSON.parseObject(postForObject);
        SendResult result = new SendResult();
        String code = parseObject.getString("code");
        int status;
        if("00".equals(code)) {
            status = 1;//成功
            result.setSuccess(true);
        }else if("05".equals(code)){
            status = 0;//被除重
        } else {
            status = 2;//其他原因
        }
        result.setResultMsg(postForObject);
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),status,postForObject));
        return result;
    }

//        public static void main(String[] args){

//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("测试林先森");
//        po.setHouse(0);
//        po.setCity("上海市");
//        po.setCompany(0);
//        po.setGetwayIncome(0);
//        po.setInsurance(0);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965140");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-500元");
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setUpdateDate(new Date());
//        Bank51Api api = new Bank51Api();
//        System.out.println(api.send(po,null));

//            Random ro = new Random();
//            int index = ro.nextInt(6);
//            System.out.println(index);
//    }
}
