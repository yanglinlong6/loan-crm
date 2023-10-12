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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 上海誉勒：上海：apiSender_20033
 */
//@Component("apiSender_20033")
public class YuLeApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(YuLeApi.class);

    private static final String code = "zdtshyltttg01";
    
    private static final String sendUrl = "http://dbz.huidaikeji.com/api/zdapi/addzdtd3";

    private static final String checkUrl = "http://dbz.huidaikeji.com/api/zdapi/colliding?mobile=";

    private static final int department_id=66;

    private static final String flag="tttg";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海誉勒】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【上海誉勒】发送异常："+e.getMessage());
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select) throws Exception {

        String url = checkUrl+ MD5Util.getMd5String(po.getMobile())+"&department_id="+department_id;
        String checkResult = HttpUtil.getForObject(url);
        log.info("【上海誉勒】验证手机号码结果：{}",checkResult);
        //{"code":200,"message":"请求成功"}
        if(200 != JSONUtil.toJSON(checkResult).getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海誉勒】分发重复："+checkResult));
            return new SendResult(false,"【上海誉勒】验证手机号码:重复："+checkResult);
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isEmpty(po.getName())) {
                if(StringUtils.isEmpty(parse.get("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        JSONObject data = new JSONObject();
        data.put("code",code);
        data.put("flag",flag);
        data.put("name",po.getName());
        data.put("phone",po.getMobile());
        data.put("money", LoanAmountUtil.transform(po.getLoanAmount()));
        data.put("age",(po.getAge()==null || po.getAge() <= 0 )?30:po.getAge());
        data.put("sex",(po.getGender() == null || po.getGender() != 1)?0:1);
        data.put("house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("fund",po.getPublicFund().contains("有，")?1:0);
        data.put("social",0);
        data.put("credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("webank",0);
        data.put("tax",0);
        data.put("work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("city",po.getCity());

        String encrypt = AESUtil.java_openssl_encrypt(data.toJSONString(),"DbzTJ2txzXiaoLia").toLowerCase();
        data.clear();
        data.put("data",encrypt);
        String result  = HttpUtil.postForJSON(sendUrl,data);
        log.info("【上海誉勒】分发结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if(500 == resultJSON.getIntValue("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海誉勒】成功"+result));
            return new SendResult(true,"【上海誉勒】:"+result);
        }
        String msg = resultJSON.getString("msg");
        if("电话号码重复".equals(msg)){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海誉勒】重复"+result));
            return new SendResult(false,"【上海誉勒】"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海誉勒】失败"+result));
        return new SendResult(false,"【上海誉勒】"+result);
    }


//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("测试朋友圈");
//        po.setMobile("13632965532");
//        po.setCity("上海市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setAge(33);
//        po.setChannel("tt0012");
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setCreateBy("car");
//        po.setUpdateDate(new Date());
//        ApiSender api = new YuLeApi();
//        System.out.println(api.send(po,null));
//    }
}
