package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.utlis.Md5Util;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.common.utils.http.HttpClientProxy;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 苏州金悦恒
 */
@Component("apiSender_20051")
public class SZHJinYueHengApi implements ApiSender {

    private static final Logger log = LoggerFactory.getLogger(SZHJinYueHengApi.class);
    private static final String checkUrl = "http://admin.chengdadai.com/index.php/api/index/check.html?MobilePhone=%s&key=n837h0MEWriuYceLpxYwoXGp8MbvK43NOUjwcnbNSuHoMvIzb3";
    private static final String sendUrl = "http://admin.chengdadai.com/index.php/api/index/add.html?channel=%s&name=%s&mobile=%s&city=%s&money=%s&key=%s&sex=%s&has_house=%s&has_car=%s&has_baodan=%s&has_gongjijin=%s&has_weilidai=无&has_job=%s";
    private static final String key = "n837h0MEWriuYceLpxYwoXGp8MbvK43NOUjwcnbNSuHoMvIzb3";
    private static final String aesKey = "1234567890123456";
    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            String channel = po.getChannel();
            if(StringUtils.isNotBlank(channel) && channel.toLowerCase().contains("ttt")){
                return new SendResult(false,"[苏州金悦恒]不接收头条");
            }
            return sendResult(po, select);
        } catch (Exception e) {
            log.error("[苏州金悦恒]分发异常:{}", e.getMessage(), e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(), po.getId(), 2, "【苏州金悦恒】分发未知异常：" + e.getMessage()));
            return new SendResult(false, "[苏州金悦恒]分发异常:" + e.getMessage());
        }
    }


    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws Exception {
        isHaveAptitude(po);

        String url = String.format(checkUrl,Md5Util.encryptMd5(po.getMobile()));
        Map<String, String> httpHeader = new HashMap<>();
        httpHeader.put(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
        // {"errNum":-1,"errMsg":"\u672a\u8bc6\u522b\u5230\u53ef\u7528\u6570\u636e","retData":[]}
        String result = HttpClientProxy.doPost(url, new JSONObject(),"UTF-8", 3000, httpHeader);
        log.info("【金悦恒】撞库结果：{}",result);
        JSONObject json = JSONUtil.toJSON(result);
        String msg = json.getString("errMsg");
        int code = json.getIntValue("errNum");
        if(0 != code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【金悦恒】撞库重复:"+result));
            return new SendResult(false,"【金悦恒】撞库重复："+msg);
        }
        String channel = null;
        if(StringUtils.isBlank(po.getChannel())){
            channel = "朋友圈";
        }else {
            if(po.getChannel().contains("ttt")){
                channel = "头条";
            }else if(po.getChannel().contains("baidu")){
                channel = "百度推广";
            }else{
                channel = "朋友圈";
            }
        }
        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        int money = LoanAmountUtil.transform(po.getLoanAmount());
        String sex = null == po.getGender()?"未知":po.getGender() == 1?"男":"女";
        String has_house = JudgeUtil.in(po.getHouse(),1,2)?"有":"无";
        String has_car = JudgeUtil.in(po.getCar(),1,2)?"有":"无";
        String baodan = JudgeUtil.in(po.getInsurance(),1,2)?"有":"无";
        String gongjijin = po.getPublicFund().contains("有，")?"有":"无";
        String job = JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无";
        url = String.format(sendUrl,channel,po.getName(),po.getMobile(),city,money,key,sex,has_house,has_car,baodan,gongjijin,job);
        result = HttpClientProxy.doPost(url, new JSONObject(),"UTF-8", 3000, httpHeader);
        log.info("【金悦恒】分发结果：{}",result);
        json = JSONUtil.toJSON(result);
        msg = json.getString("errMsg");
        code = json.getIntValue("errNum");
        if(0 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【金悦恒】发送成功:"+msg));
            return new SendResult(true,"【金悦恒】分发成功："+msg);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【金悦恒】发送失败:"+msg));
        return new SendResult(false,"【金悦恒】分发失败："+msg);
    }


    public static void main(String[] args){
        UserAptitudePO po = new UserAptitudePO();
        po.setUserId(null);
        po.setName("张先森测试朋友圈2");
        po.setMobile("13632965538");
        po.setCity("苏州市");
        po.setLoanAmount("5000000");
        po.setCompany(0);
        po.setPublicFund("公积金有，");
        po.setCar(0);
        po.setHouse(0);
        po.setInsurance(0);
        po.setGetwayIncome(0);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setAge(30);
        po.setGender(1);
        po.setChannel("112233_test");
        po.setUpdateDate(new Date());
        ApiSender api = new SZHJinYueHengApi();
        System.out.println(api.send(po,null));

    }


}
