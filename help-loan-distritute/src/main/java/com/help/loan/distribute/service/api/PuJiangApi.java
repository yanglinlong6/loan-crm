package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
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
import java.util.HashMap;
import java.util.Map;

@Component("apiSender_20025")
public class PuJiangApi implements  ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(PuJiangApi.class);

    private static final String checkUrl = "https://api.zhjyjr.com/api/user/check";

    private static final String URL = "https://api.zhjyjr.com/api/yhzd/addCust";

    private static final String channel = "chl76";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            if(StringUtils.isNotBlank(po.getChannel()) && !po.getChannel().startsWith("ttt")){
                return new SendResult(false,"【浦江】非头条客户");
            }
            isHaveAptitude(po);
            return send2(po,select);
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【浦江】分发异常："+e.getMessage()));
            return new SendResult(false,"【浦江】分发异常："+e.getMessage());
        }
    }

    public SendResult send2(UserAptitudePO po, UserDTO select) throws InterruptedException {
//        String judgeRepeatResult = HttpUtil.getForObject(JudgeRepeatUrl+MD5Util.getMd5String(po.getMobile()));
        JSONObject checkData = new JSONObject();
        checkData.put("phoneNo",MD5Util.getMd5String(po.getMobile()));
        checkData.put("registerFrom",channel);
        String judgeRepeatResult = HttpUtil.postForJSON(checkUrl,checkData);
        LOG.info("【浦江】撞库结果：{}",judgeRepeatResult);
        JSONObject judgeJson = JSONUtil.toJSON(judgeRepeatResult);
        //{"resMap":{"errorInfo":"手机号预判重校验通过","errorNo":0}}
        //{"businessCode":"0000","code":"0000","msg":"success"}
        if("0000".equals(judgeJson.getString("code"))){
            Map<String, Object> content = new HashMap<>();
            String name = po.getName();
            if(StringUtils.isBlank(name) && null != select){
                JSONObject userInfo = JSONUtil.toJSON(WechatCenterUtil.getUserInfo(po.getUserId(),"",""));
                name = EmojiFilter.filterEmoji(userInfo.getString("nickname"),po.getUserId());
            }
            content.put("cust_name",name);
            content.put("mobile",po.getMobile());
            content.put("age",po.getAge() == null?"30":po.getAge().toString());
            content.put("sex",po.getGender().toString());
            content.put("city",po.getCity());
            Integer loanAmount = LoanAmountUtil.transform(po.getLoanAmount());
            if(loanAmount.intValue() <= 50000)
                content.put("apply_limit", "50000");
            else content.put("apply_limit",loanAmount.toString());
            content.put("channel_source",channel);
            content.put("media_source","");
            if(JudgeUtil.in(po.getHouse(),1,2)){
                content.put("has_house","1");
            }else{
                content.put("has_house","0");
            }
            if(JudgeUtil.in(po.getCar(),1,2)){
                content.put("has_car","1");
            }else
                content.put("has_car","0");

            if(1 == po.getCompany().intValue())
                content.put("has_company","1");
            else  content.put("has_company","0");

            if(JudgeUtil.in(po.getCreditCard(),1,2)){
                content.put("has_credit","1");
            }else content.put("has_credit","0");

            if(JudgeUtil.in(po.getInsurance(),1,2)){
                content.put("has_policy","1");
            }else content.put("has_policy","0");

            content.put("has_social","0");

            if(StringUtils.isNotBlank(po.getPublicFund()) && po.getPublicFund().contains("有，")){
                content.put("has_fund","1");
            }else content.put("has_fund","0");


            if(JudgeUtil.in(po.getOccupation().intValue(),1,2,3) || JudgeUtil.in(po.getGetwayIncome(),1,2))
                content.put("has_work","1");
            else  content.put("has_work","0");

            content.put("has_tax","0");
            content.put("wedebt_limit","0");
            JSONObject data = new JSONObject();
            data.put("enctryt_content", PuJiang2Api.Crypto.encrypt(JSONUtil.toJsonString(content), PuJiang2Api.Crypto.key));
            // {"data":{"error_info":"申请已提交","error_no":0}}
            String result = HttpUtil.postForJSON(URL,data);
            int errorNo = JSONUtil.toJSON(result).getJSONObject("data").getIntValue("errorNo");
            if(JSONUtil.isJsonString(result) && 0 == errorNo){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,result));
                return new SendResult(true,result);
            }else if(-1 == errorNo){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,result));
                return new SendResult(false,judgeRepeatResult);
            }else{
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,result));
                return new SendResult(false,result);
            }
        }else {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,judgeRepeatResult));
            return new SendResult(false,judgeRepeatResult);
        }
    }

    public static void main(String[] args){

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("9e7710ddd0dc49c59dc3468214be44d1");
        po.setName("测试伍散人");
        po.setHouse(0);
        po.setCity("上海市");
        po.setCompany(1);
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setLoanAmount("《3-5万》");
        po.setMobile("18229491952");
        po.setOccupation(1);
        po.setPublicFund("有，个人月缴500元元");
        po.setCreditCard(1);
        po.setCar(1);
        po.setGender(1);
        po.setUpdateDate(new Date());
        ApiSender api = new PuJiangApi();
        System.out.println(api.send(po,null));


    }
}
