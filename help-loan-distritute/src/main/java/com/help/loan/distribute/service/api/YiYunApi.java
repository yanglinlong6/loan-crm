package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 杭州依耘： apiSender_20010
 * 浙江省杭州市江干区解放东路财富金融中心西楼3303室  李梦月18458280730
 */
@Component("apiSender_20010")
public class YiYunApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(YiYunApi.class);

    private static final String username = "00000056";
    private static final String password = "eyw056";

    private static final String checkUrl = "http://121.40.54.91:18180/system/source/checkMobile";
    private static final String sendApi = "http://121.40.54.91:18180/system/source/api/";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("【杭州依耘】推送异常："+e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[杭州依耘]推送异常"));
            return new SendResult(false,"[杭州依耘]推送异常："+e.getMessage());
        }
    }

    public SendResult sendResult(UserAptitudePO po, UserDTO select){

        JSONObject checkJson = new JSONObject();
        checkJson.put("mobile", MD5Util.getMd5String(po.getMobile()));
        checkJson.put("username",username);
        checkJson.put("password",MD5Util.getMd5String(username+password));

        String checkResult = HttpUtil.postForJSON(checkUrl,checkJson);
        LOG.info("[杭州依耘]验证重复结果：手机-{}：{}",po.getMobile(),checkResult);
        if(JSONUtil.isJsonString(checkResult) && 0 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[杭州依耘]推送重复："+checkResult));
            return new SendResult(false,checkResult);
        }
        JSONObject data = new JSONObject();
        data.put("username",username);
        data.put("password",MD5Util.getMd5String(username+password));
        String name = po.getName();
        if(StringUtils.isBlank(name) && null != select){
            JSONObject userInfo = JSONUtil.toJSON(WechatCenterUtil.getUserInfo(po.getUserId(),"",""));
            name = EmojiFilter.filterEmoji(userInfo.getString("nickname"),po.getUserId());
        }
        data.put("name", name+Heng+parseAccount(po.getChannel()));
        data.put("mobile", po.getMobile());
        data.put("age", po.getAge());
        data.put("city", po.getCity());
        data.put("age", po.getAge());
        data.put("sex", po.getGender());
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("house", 2);
            data.put("houseType",1);
        }else {
            data.put("house", 1);
            data.put("houseType",7);
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("car",2);
        }else data.put("car",1);
        data.put("sheBao",1);
        data.put("sheBaoTime", 1);
        if(po.getPublicFund().contains("有，")){
            data.put("fund",2);
        }else data.put("fund",1);

        if(JudgeUtil.in(po.getCreditCard(),1,2)){
            data.put("xyCard",2);
        }else data.put("xyCard",1);
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("shouXian",2);
        }else data.put("shouXian",1);

        data.put("shouXian", 1);
        data.put("loanAmount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            data.put("payway",1);
        }else data.put("payway",2);

        String response = HttpUtil.postForJSON(sendApi,data);
        LOG.info("[杭州依耘]分发结果：{}",response);
        if(JSONUtil.isJsonString(response)){
            if(0 == JSONUtil.toJSON(response).getIntValue("code")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[杭州依耘]推送成功："+response));
                return new SendResult(true,"[杭州依耘]推送成功："+response);
            }else if(201 == JSONUtil.toJSON(response).getIntValue("code")){
                LOG.error("[杭州依耘]推送重复：手机-{}已存在：{}",po.getMobile(),checkResult);
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[杭州依耘]推送重复："+checkResult));
                return new SendResult(false,"[杭州依耘]推送重复："+response);
            }
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[杭州依耘]推送失败："+response));
        return new SendResult(false,"[杭州依耘]推送失败："+response);
    }


//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("3a1d24ac4761473abf9f4017b40288f6");
//        po.setName("伍散人测试请忽略");
//        po.setHouse(0);
//        po.setAge(35);
//        po.setCity("杭州市");
//        po.setCompany(0);
//        po.setGetwayIncome(0);
//        po.setInsurance(0);
//        po.setLoanAmount("《10-30万》");
//        po.setMobile("13632965150");
//        po.setOccupation(1);
//        po.setPublicFund("无");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(1);
//        po.setCar(1);
//        po.setChannel("zxf-ttt-30");
//        po.setType(DistributeConstant.LoanType.CREDIT);
//        po.setUpdateDate(new Date());
//        ApiSender api = new YiYunApi();
//        System.out.println(api.send(po,null));
//
//    }
}
