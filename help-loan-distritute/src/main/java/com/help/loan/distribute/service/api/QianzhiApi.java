package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 钱智:速贷中心：apiSender_20027
 */
@Component("apiSender_20027")
public class QianzhiApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(QianzhiApi.class);

    private static final String CHECK_URL = "https://jiekou.bangdai.com/sem/check_loan_phone.html";

    private static final String URL = "https://jiekou.bangdai.com/sem/loan_do.html";

    private static final String source = "bzwy02";

    private static final String source_tt = "bzwy02-ttbj";

    private static final String key = "8H8*9nm7#G(NG5n[6A2x4SLn";

    private static final String key_tt = ",2odn5ofL^B!J4mTwK?uv,?o";

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            isHaveAptitude(po);
            if("上海市".equals(po.getCity()) && StringUtils.isNotBlank(po.getChannel()) && !po.getChannel().startsWith("ttt")){
                return new SendResult(false,"速贷上海不接收朋友圈客户:"+po.getChannel());
            }
            return send1(po, select);
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
            return new SendResult(false, "[钱智:速贷中心]分发异常：" + e.getMessage());
        }

    }


    private SendResult send1(UserAptitudePO po, UserDTO select) {
        String phoneMd5 = MD5Util.getMd5String(po.getMobile());
        int time  = DateUtil.to10();
        String checkUrl = null;
        if(po.getCity().equals("北京市") && StringUtils.isNotBlank(po.getChannel())  && po.getChannel().toLowerCase().contains("tt")){
            String sign = MD5Util.getMd5String(phoneMd5+time+key_tt);
            checkUrl = new StringBuffer(CHECK_URL).append("?").append("phone=").append(phoneMd5).append("&time=").append(time).append("&sign=").append(sign).append("&source=").append(source_tt).toString();
        }else {
            String sign = MD5Util.getMd5String(phoneMd5+time+key);
            checkUrl = new StringBuffer(CHECK_URL).append("?").append("phone=").append(phoneMd5).append("&time=").append(time).append("&sign=").append(sign).append("&source=").append(source).toString();
        }
        String checkResponse = HttpUtil.getForObject(checkUrl);
        LOG.info("【钱智:速贷中心】验证手机号码结果：{}",checkResponse);
        JSONObject checkJson = JSONUtil.toJSON(checkResponse);
        //参数错误：{ "code": 3, "success": false, "error": "缺失参数。" }
        //重复了：{"code": 0,"success": true,"data": {"phone": 1,"time": "2019-02-01 11:11:11" }}
        //没有重复：{ "code": 0, "success": true, "data": { "phone": 0} }
        if(0 == checkJson.getIntValue("code") && checkJson.getBooleanValue("success")){
            if(checkJson.containsKey("data") && 1 == checkJson.getJSONObject("data").getIntValue("phone")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【钱智:速贷中心】手机号码重复："+checkResponse));
                return new SendResult(false,checkResponse);
            }
            return register(po,select);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【钱智:速贷中心】："+checkResponse));
        return new SendResult(false,checkResponse);
    }


    private SendResult register(UserAptitudePO po,UserDTO select){
        JSONObject data = new JSONObject();
        if(null != select && StringUtils.isBlank(po.getName())){
            JSONObject user = JSONUtil.toJSON(WechatCenterUtil.getUserInfo(po.getUserId(), "", ""));
            String name = StringUtils.isBlank(po.getName()) ? EmojiFilter.filterEmoji(user.getString("nickname")) : po.getName();
            po.setName(name);
        }
        data.put("name", po.getName());
        data.put("mobile", po.getMobile());
        String city = po.getCity();
        if(city.endsWith("市"))
            city = city.substring(0,city.length()-1);
        data.put("city",city);
        if(1 == po.getCar().intValue() || 2 == po.getCar().intValue()) {
            data.put("car", "有");
        } else
            data.put("car", "无");
        data.put("age", po.getAge());
        if(JudgeUtil.in(po.getGetwayIncome(), 1, 2) || JudgeUtil.in(po.getOccupation(), 1, 2, 3)) {
            data.put("job", "有");
            data.put("isbankpay", "是");
        } else {
            data.put("job", "无");
            data.put("isbankpay", "否");
        }
        if(JudgeUtil.in(po.getHouse(), 1, 2, 4)) {
            data.put("house", "有");
        } else
            data.put("house", "无");

        if(JudgeUtil.in(po.getInsurance(), 1, 2)) {
            data.put("baodan_is", "有");
        } else
            data.put("baodan_is", "无");

        String sex = "男";
        if(po.getGender() == null) {
            sex = "男";
        } else if(po.getGender() == 1) {
            sex = "男";
        } else if(po.getGender() == 2) {
            sex = "女";
        }
        data.put("sex", sex);
        data.put("money", Integer.valueOf(LoanAmountUtil.transformToWan(po.getLoanAmount())));
        if(po.getCity().equals("北京市") &&  StringUtils.isNotBlank(po.getChannel())  && po.getChannel().toLowerCase().contains("tt")){
            data.put("source", source_tt);// 固定值
        }else{
            data.put("source", source);// 固定值
        }
        if(JudgeUtil.in(po.getPublicFund(), "0", "没有")) {
            data.put("gongjijin", "无");
        } else {
            data.put("gongjijin", "有");
        }
        data.put("shebao", "无");
        data.put("ip", "47.98.35.160");
        if(JudgeUtil.in(po.getCreditCard(), 1, 2))
            data.put("credit_card", "有");
        else data.put("credit_card", "无");
        data.put("meiti", "邦正");
        data.put("time", DateUtil.to10());
        data.put("weili", "无");

        String response = HttpUtil.postForJSON(URL, data.toJSONString());
        LOG.info("[钱智:速贷中心]用户：{},分发结果：{}", po.getMobile(), response);
        JSONObject responseJson = JSONUtil.toJSON(response);
        //{"code":"n","success":false,"error":"当前不接收合作渠道数据"}
        //{"code":12,"success":false,"error":"当前不接收合作渠道数据"}
        //{"code":0,"success":true,"data":{"phone":1,"time":"2020-08-16 16:18:16"}}
        // {"code":0,"data":{"id":6927255},"success":true}
        int code = responseJson.getIntValue("code");
        JSONObject dataJson = responseJson.getJSONObject("data");
        if(0 == code && responseJson.getBooleanValue("success") && dataJson.containsKey("id")) {
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【钱智:速贷中心】发送成功："+response));
            return new SendResult(true, "【钱智:速贷中心】发送成功："+response);
        }
        if(12 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【钱智:速贷中心】发送失败："+response));
            return new SendResult(false, "【钱智:速贷中心】发送失败:"+response);
        }
        if(7 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【钱智:速贷中心】发送失败："+response));
            return new SendResult(false, "【钱智:速贷中心】发送失败:"+response);
        }
        if(dataJson.containsKey("phone")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【钱智:速贷中心】手机号码重复："+response));
            return new SendResult(false, "【钱智:速贷中心】撞库:"+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【钱智:速贷中心】发送失败："+response));
        return new SendResult(false, response);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("71f63d78911a4ab3806d69eccccb0642");
//        po.setName("伍散人测试");
//        po.setAge(33);
//        po.setMobile("13049692803");
//        po.setCity("北京市");
//        po.setLoanAmount("50000");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("无");
//        po.setGetwayIncome(1);
//        po.setInsurance(0);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setChannel("ttt");
//        po.setUpdateDate(new Date());
//        QianzhiApi api = new QianzhiApi();
//        System.out.println(api.send(po,null));
//    }

}
