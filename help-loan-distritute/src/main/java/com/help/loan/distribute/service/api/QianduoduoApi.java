package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component("apiSender_10011")
public class QianduoduoApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(QianduoduoApi.class);


    private static final String sendUrl = "http://118.89.92.246:8080/api/customer/add";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return send2(po,select);
        }catch (Exception e){
            log.error("【钱多多[1]分发异常：{}】",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【钱多多-1】分发异常:"+e.getMessage()));
            return new SendResult(false,"钱多多[1]分发异常："+e.getMessage());
        }

    }


    private SendResult send2(UserAptitudePO po, UserDTO select){

        isHaveAptitude(po);

        if(null != select && StringUtils.isBlank(po.getName())){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(parse.get("openid").toString())) {
                po.setName("公众号用户");
            } else {
                po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
            }
        }

        JSONObject data = new JSONObject();
        data.put("name",po.getName());
        data.put("sex",(null == po.getGender() || JudgeUtil.in(po.getGetwayIncome(),0,2)) ? "女":"男");
        data.put("mobile",po.getMobile());
        data.put("money", LoanAmountUtil.transformToWan(po.getLoanAmount()));
        data.put("city",po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity());
        data.put("age",(null == po.getAge() || po.getAge() <=0)?30:po.getAge());
        data.put("shebao","无");
        data.put("gongjijin",po.getPublicFund().contains("有，")?"有":"无");
        data.put("baodan_is",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        data.put("license",JudgeUtil.in(po.getCompany(),1)?"有":"无");
        data.put("house",JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        data.put("car",JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        data.put("isbankpay",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        data.put("source","tgfx");
        data.put("meiti","");
        data.put("remark","");

        //3：重复申请
        //5：失败
        //0：成功
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        //{"retCode":"017","retMsg":"客户信息申请频繁","retData":null}
        String response = HttpClientProxy.doPost(sendUrl, data, "UTF-8", 3000, httpHeader);
        log.info("【钱多多-1】分发结果：{}",response);
        JSONObject json = JSONUtil.toJSON(response);
        int code = json.getIntValue("code");
        if(code == 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【钱多多-1】分发成功:"+response));
            return new SendResult(true,"【钱多多-1】分发成功："+response);
        }
        if(code == 3){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【钱多多-1】分发重复:"+response));
            return new SendResult(false,"【钱多多-1】分发重复："+response);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【钱多多-1】分发失败:"+response));
        return new SendResult(false,"【钱多多-1】分发失败："+response);
    }

//    public static void main(String[] args){
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("71f63d78911a4ab3806d69eccccb0642");
//        po.setName("测试");
//        po.setMobile("13049692800");
//        po.setCity("上海市");
//        po.setGender(1);
//        po.setLoanAmount("50000");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(1);
//        po.setPublicFund("无");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setUpdateDate(new Date());
//        QianduoduoApi api = new QianduoduoApi();
//        System.out.println(api.send(po,null));
//    }
}
