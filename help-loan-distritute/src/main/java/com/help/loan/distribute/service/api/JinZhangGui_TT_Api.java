package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 金掌柜-头条：
 */
@Component("apiSender_20023")
public class JinZhangGui_TT_Api implements  ApiSender {

    private static final Logger log = LoggerFactory.getLogger(JinZhangGui_TT_Api.class);

    private static final int cid = 241;

    private static final String src = "今日头条";

    private static final String key = "08f5b8786d5d88a17f0c50da5b277b1b";

    private static final String url = "http://system.jzgloan.com/api/json_reg";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
//            if(org.apache.commons.lang3.StringUtils.isNotBlank(po.getChannel()) &&  po.getChannel().toLowerCase().contains("tt")){
//                return send2(po,select);
//            }
//            return new SendResult(false,"【金掌柜】推送失败：只接收头条客户");
            return send2(po,select);
        }catch (Exception e){
            String msg = "【金掌柜】推送异常:"+e.getMessage();
            log.error(msg,e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【金掌柜】推送结果："+msg));
            return new SendResult(false,msg);
        }

    }


    public SendResult send2(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        if(null != select && StringUtils.isEmpty(po.getName())){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(parse.get("openid"))) {
                po.setName("公众号用户");
            } else {
                po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
            }
        }

        JSONObject data = new JSONObject();
        data.put("cid",cid);
        data.put("v", MD5Util.getMd5String(po.getMobile()+cid+key));
        data.put("src",src);
        data.put("name",po.getName());
        data.put("age",po.getAge() == 0?30:po.getAge());
        data.put("sex",po.getGender() == 0?2:1);
        data.put("mobile",po.getMobile());
        data.put("ip","127.0.0.1");
        data.put("city",po.getCity().substring(0,po.getCity().length()-1));
        data.put("loan_amount", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        if(JudgeUtil.in(po.getHouse(),1,2)){
            data.put("houses",po.getHouse());
        }else
            data.put("houses",12);
        if(JudgeUtil.in(po.getCar(),1,2)){
            data.put("car",po.getCar());
        }else
            data.put("car",3);
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            data.put("life_policy",1);
        }else data.put("life_policy",2);

        if(po.getPublicFund().contains("有，")){
            data.put("epf_time",2);
        }else data.put("epf_time",5);
        if(1 == po.getGetwayIncome())
            data.put("income_range",2);
        if(2 == po.getGetwayIncome())
            data.put("income_range",1);
        if(JudgeUtil.in(po.getCreditCard(),1,2))
            data.put("credit_card",1);
        else data.put("credit_card",2);

        data.put("social_security",5);

        String response = HttpUtil.postForJSON(url,data);
        JSONObject json = JSONUtil.toJSON(response);
        String msg = json.getString("msg");
        log.info("【金掌柜】推送结果："+msg);
        if("0".equals(json.getString("errno"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,msg));
            return new SendResult(true,"【金掌柜】推送成功："+response);
        }else if("5".equals(json.getString("errno"))){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【金掌柜】推送重复："+msg));
            return new SendResult(false,response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【金掌柜】推送啊失败："+msg));
        return new SendResult(false,response);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("a4ef7b032cf74469a3390e6d73132c7b");
//        po.setName("伍散人测试请忽略");
//        po.setHouse(1);
//        po.setAge(35);
//        po.setCity("杭州市");
//        po.setCompany(0);
//        po.setInsurance(0);
//        po.setLoanAmount("100000");
//        po.setMobile("13851583282");
//        po.setOccupation(1);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setOccupation(1);
//        po.setGetwayIncome(1);
//        po.setCreditCard(0);
//        po.setCar(1);
//        po.setGender(2);
//        po.setChannel("ttt");
//        po.setUpdateDate(new Date());
//        ApiSender api = new JinZhangGui_TT_Api();
//        System.out.println(api.send(po,null));
//    }
}
