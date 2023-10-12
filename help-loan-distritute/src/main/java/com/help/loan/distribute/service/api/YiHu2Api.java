package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.*;
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

import java.util.Date;

/**
 * 上海翊浒：apiSender_10066
 */
@Component("apiSender_10066")
public class YiHu2Api implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(YiHu2Api.class);

    private static final String checkUrl = "https://paas1-api.zgzdzx.cn/API/CustExists.ashx";

    private static final String sendUrl = "https://paas1-api.zgzdzx.cn/API/Customer.ashx";

    private static final Integer channle = 110002;

    private static final String key = "kys98722&32@2kf";

    private static final String adsid = "pyq";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            LOG.error("【翊浒-2】分发异常",e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【翊浒-2】:"+e.getMessage()));
            return new SendResult(false,"【翊浒-2】分发异常："+e.getMessage());
        }

    }


    private SendResult send2(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        //验证手机号码是否已经存在
        JSONObject data = new JSONObject();
        data.put("Sign",MD5Util.getMd5String(MD5Util.getMd5String(po.getMobile())+key));
        data.put("Md5code",MD5Util.getMd5String(po.getMobile()));
        data.put("ChannelId",channle);
        String checkResult = HttpUtil.postForJSON(checkUrl,data);
        LOG.info("【翊浒-2】验证手机号码是否存在：{}",checkResult);
        if(JSONUtil.isJsonString(checkResult) && 1 != JSONUtil.toJSON(checkResult).getIntValue("ResultCode")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【翊浒-2】:"+checkResult));
            return new SendResult(false,"【翊浒-2】手机已存在:"+checkResult);
        }

        String name = po.getName();
        if(null != select && StringUtils.isBlank(name)){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(po.getName())) {
                if(StringUtils.isBlank(parse.getString("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }

        data.clear();
        data.put("Sign",MD5Util.getMd5String(po.getMobile()+key+adsid));
        data.put("Name",name);
        data.put("Phone",po.getMobile());
        data.put("Sex",po.getGender());
        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        data.put("City",city);
        data.put("IsMarried",0);
        data.put("CareerType",0);
        int monthlySalary = 0;
        if(JudgeUtil.in(po.getGetwayIncome(),1)){
            monthlySalary = 2;
        }else if(JudgeUtil.in(po.getGetwayIncome(),2)){
            monthlySalary = 1;
        }
        data.put("MonthlySalary",monthlySalary);

        if(po.getPublicFund().contains("有，")){
            data.put("HousingFund",2);
        }else
            data.put("HousingFund",0);

        data.put("SheBao",0);
        if(JudgeUtil.in(po.getHouse(),1)){
            data.put("HouseProperty",2);
        }else if(JudgeUtil.in(po.getHouse(),2)){
            data.put("HouseProperty",1);
        }else
            data.put("HouseProperty",0);
        data.put("PropertyOwnership",0);
        if(JudgeUtil.in(po.getCar(),1)){
            data.put("CarProperty",2);
        }else if(JudgeUtil.in(po.getCar(),2)){
            data.put("CarProperty",1);
        }else
            data.put("CarProperty",0);

        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("IsHasBx",1);
        }else
            data.put("IsHasBx",2);
        data.put("RecentCredit",0);
        data.put("LoanAmount", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        data.put("IsInterview",2);
        data.put("HasWeilidai",0);
        data.put("AdvType",0);
        data.put("ChannelId",channle);
        data.put("AdsId",adsid);
        data.put("CreateDate", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));

        String result = HttpUtil.postForJSON(sendUrl,data);
        LOG.info("【翊浒-2】发送数据结果：{}",result);
        if(!JSONUtil.isJsonString(result)){
            LOG.info("【翊浒-2】发送数据失败：{},返回格式错误",result);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【翊浒-2】发送数据返回格式错误:"+checkResult));
            return new SendResult(false,"【翊浒-2】发送数据返回格式错误："+result);
        }
        JSONObject jsonData = JSONUtil.toJSON(result);
        String message = jsonData.getString("ResultMsg");
        if(0 == jsonData.getIntValue("ResultCode")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【翊浒-2】发送数据接收失败:"+message));
            return new SendResult(false,"【翊浒-2】发送数据接收失败："+message);
        }else if(2 == jsonData.getIntValue("ResultCode")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【翊浒-2】发送数据数据重复:"+message));
            return new SendResult(false,"【翊浒-2】发送数据数据重复："+message);
        }else if(1 == jsonData.getIntValue("ResultCode")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【翊浒-2】发送数据成功:"+message));
            return new SendResult(true,"【翊浒-2】发送数据成功："+message);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【翊浒-2】发送数据失败:"+message));
        return new SendResult(false,"【翊浒-2】发送数据失败："+message);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("29e78c98feee4aeb9f9677d9e2dfa251");
//        po.setName("刘芬");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("上海市");
//        po.setCompany(0);
//        po.setInsurance(1);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("18301799110");
//        po.setOccupation(1);
//        po.setPublicFund("有，");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setOrgId(10044l);
//        po.setUpdateDate(new Date());
//        YiHu2Api api = new YiHu2Api();
//        System.out.println(api.send(po,null));
//
//    }
}
