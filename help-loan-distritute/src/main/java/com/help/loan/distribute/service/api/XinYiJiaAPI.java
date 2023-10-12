package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.crm.CrmData;
import com.help.loan.distribute.service.api.crm.ParamBO;
import com.help.loan.distribute.service.api.crm.SignUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 云南信益佳商务信息咨询有限公司：apiSender_10038
 */
@Component("apiSender_10077")
public class XinYiJiaAPI implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(XinYiJiaAPI.class);

    String appid = "66bfa827731314181fe13e8e75461733";
    String secret = "862fec85d10346edaca4fe019ca08b53";
    int  sourceId = 9634;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【云南信益佳】:"+e.getMessage()));
            return new SendResult(false,"【云南信益佳】分发失败");
        }
    }

    private SendResult send2(UserAptitudePO po, UserDTO select){

        return send3(po,select);
    }

    private static final Random random2 = new Random(4);
    @Override
    public boolean isHaveAptitude(UserAptitudePO po) {
        if(po.getPublicFund().contains("有，"))
            return true;
        if(JudgeUtil.in(po.getCar(),1,2))
            return true;
        if(JudgeUtil.in(po.getHouse(),1,2))
            return true;
        if(JudgeUtil.in(po.getInsurance(),1,2))
            return true;
        if(JudgeUtil.in(po.getCompany(),1))
            return true;
        int index = random2.nextInt(4);
        switch (index){
            case 0:
                po.setPublicFund("有，个人月缴800元以上");
                break;
            case 1:
                po.setHouse(1);
                break;
            case 2:
                po.setCar(1);
                break;
            case 3:
                po.setInsurance(1);
                break;
            case 4:
                po.setCompany(1);
                break;
            default:po.setCompany(1);;
        }
        return false;
    }

    private SendResult send3(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        CrmData data = new CrmData();
        data.setAppid(appid);
        data.setNonc(UUID.randomUUID().toString());
        data.setTimestamp(System.currentTimeMillis());
        data.setEncryption(0);
        if(null != select){
            if(StringUtils.isEmpty(po.getName())) {
                String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
                JSONObject parse = JSON.parseObject(userInfo);
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        ParamBO param = new ParamBO();
        param.setName(po.getName());
        param.setGender(1);
        param.setMobile(po.getMobile());
        param.setCity(po.getCity());
        param.setAge(0==po.getAge()?32:po.getAge());
        param.setQuota(LoanAmountUtil.transform(po.getLoanAmount()));
        if(po.getPublicFund().contains("有，"))
            param.setFund(1);
        else
            param.setFund(0);
        //车
        if(JudgeUtil.in(po.getCar(),1,2))
            param.setCar(1);
        else param.setCar(2);
        //房
        if(JudgeUtil.in(po.getHouse(),1,2))
            param.setHouse(1);
        else param.setHouse(2);

        //保单
        if(JudgeUtil.in(po.getInsurance(),1,2))
            param.setPolicy(1);
        else param.setPolicy(2);
        param.setLifeInsurance(0);
        if(JudgeUtil.in(po.getCompany(),1))
            param.setBusinessLicense(1);
        else param.setBusinessLicense(2);
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            param.setPayroll(1);
        }else
            param.setPayroll(0);
        param.setRemark(getInfo(po));
        param.setQualification(getInfo(po));
        param.setSourceId(sourceId);
        data.setParams(JSONUtil.toJsonString(param));
        JSONObject json = JSONUtil.toJSON(data);
        json.put("secret",secret);
        Set<String> keySet = json.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        keyList.sort((o1,o2) -> o1.compareTo(o2));
        StringBuffer signString = new StringBuffer();
        for(int i=0;i<keyList.size();i++){
            String key = keyList.get(i);
            if(i == 0)
                signString.append(key).append("=").append(json.get(key));
            else
                signString.append("&").append(key).append("=").append(json.get(key));
        }
        data.setSign(SignUtil.sign(signString.toString()));
        String result = HttpUtil.postForJSON("Http://crm.daofen100.com/crm/customer/media/receive", JSONUtil.toJsonString(data));
        if(JSONUtil.isJsonString(result) && "0".equals(JSONUtil.toJSON(result).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
            return new SendResult(true,result);
        }else if("000001".equals(JSONUtil.toJSON(result).getString("code"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【云南信益佳】手机号已存在"));
        }else{
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
        }
        return new SendResult(false,result);
    }

    private java.lang.String getInfo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getCompany() ==1){
            memo.append("有营业执照").append(",");
        }
        if(po.getPublicFund().contains("有，")){
            memo.append("有").append("，");
        }
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发工资").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有商品房").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业寿险保单").append(",");
        }
        return memo.toString();
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("71f63d78911a4ab3806d69eccccb0642");
//        po.setName("测试张2");
//        po.setMobile("13632965568");
//        po.setCity("昆明市");
//        po.setAge(35);
//        po.setGender(1);
//        po.setLoanAmount("50000");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setCompany(1);
//        po.setPublicFund("无");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setUpdateDate(new Date());
//        XinYiJiaAPI api = new XinYiJiaAPI();
//        System.out.println(api.send(po,null));
//    }
}
