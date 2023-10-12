package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientUtil;
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

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 麦谷：apiSender_10038
 */
@Component("apiSender_10027")
public class MaiguAPI implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(MaiguAPI.class);

    String appid = "fe067f2adb09a78477728a1a7e27275a";
    String secret = "aef2ef28a4854be7be1c1da7e7242202";
    int  sourceId = 9633;

    private static final String URL = "http://39.105.66.44/api/addCustomerMGOneInfo";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send1(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【麦谷-北京】:"+e.getMessage()));
            return new SendResult(false,"【麦谷-北京】分发失败");
        }

    }

    public  SendResult send1(UserAptitudePO po, UserDTO select) throws UnsupportedEncodingException {

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("age",(null == po.getAge() || po.getAge() <=0) ? 28: po.getAge());
        data.put("phone",po.getMobile());
        data.put("quota",LoanAmountUtil.transformToWan(po.getLoanAmount()));
        if(po.getGetwayIncome() == 1){
            data.put("salary",8000);
        }else if(po.getGetwayIncome() == 2) data.put("salary",5000);
        else data.put("salary",0);

        data.put("city",po.getCity());
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("hasinsurance",1);
        }else data.put("hasinsurance",0);

        if(po.getPublicFund().contains("有，")){
            data.put("hasaccumulation",1);
        }else data.put("hasaccumulation",0);

        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("hashouse",1);
        }else data.put("hashouse",0);

        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("hascar",1);
        }else data.put("hascar",0);

        String result = HttpClientUtil.doPostForm(URL,data,"UTF-8");
        LOG.info("【麦谷-北京】分发结果：{}",result);
        String code = JSONUtil.toJSON(result).getString("code");
        if("1".equals(code)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【麦谷-北京】分发成功:"+result));
            return new SendResult(true,"【麦谷-北京】分发成功："+result);
        }else{
            // {"code":"0","msg":"添加失败,手机号信息已存在\t"}
            String msg = JSONUtil.toJSON(result).getString("msg");
            if(msg.contains("手机号信息已存在")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【麦谷-北京】分发重复:"+result));
                return new SendResult(false,"【麦谷-北京】分发重复："+result);
            }
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【麦谷-北京】分发失败:"+result));
            return new SendResult(false,"【麦谷-北京】分发失败："+result);
        }
    }


    private SendResult send2(UserAptitudePO po, UserDTO select){

        return send3(po,select);
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

//        param.setRemark("个人公积金月缴500以上，有工作，银行代发5000以上");
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
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【麦谷】手机号已存在"));
        }else
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
        return new SendResult(false,result);
    }
}
