package com.crm.service.sms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.service.sms.model.MsgRecPO;
import com.crm.util.AppContextUtil;
import com.crm.util.HttpUtil;
import com.crm.util.JSONUtil;
import com.crm.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YunSms implements SmsApi {

    private static final Logger LOG = LoggerFactory.getLogger(YunSms.class);

    private static final String url = "http://120.79.81.207:9090/sms/batch/v1";

    private static final String appkey = "JHbz";

    private static final String appCode = "1000";

    private static final String appSecret = "D0sr4H";

    @Override
    public boolean sendCode(String mobile, String code) {
        return false;
    }

    @Override
    public boolean sendCode2(String mobile, String code, String domain2) {
        return false;
    }

    @Override
    public boolean sendCode(String mobile, String code, String templateCode) {
        return false;
    }

    @Override
    public boolean sendMessage(String mobile, String message) {
        return false;
    }

    @Override
    public boolean sendMessage(String mobile, String message, String createBy) {
        if(StringUtils.isBlank(mobile) || StringUtils.isBlank(message)){
            LOG.info("【沃动科技】发送消息失败：手机号码：{}-{}",mobile,message);
            return false;
        }
        long timestamp = System.currentTimeMillis();
        JSONObject data = new JSONObject();
        data.put("appkey",appkey);
        data.put("appcode",appCode);
        data.put("sign", MD5Util.getMd5String(appkey+appSecret+timestamp).toLowerCase());
        data.put("timestamp",timestamp);
        data.put("phone",mobile);
        data.put("msg",message);
        data.put("extend","");

        String result = HttpUtil.postForObject(url,data);
        LOG.info("{}-{},短信发送结果:{}",mobile,message,result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        if("00000".equals(resultJSON.getString("code"))){
            JSONArray resultArray = resultJSON.getJSONArray("result");
            if(null == resultArray
                    || resultArray.isEmpty()
                    || "00000".equals(resultArray.getJSONObject(0).getString("status"))){
                SmsService  smsService = AppContextUtil.get().getBean("smsServiceImpl",SmsService.class);
                if(null != smsService)
                    smsService.addMsgRec(new MsgRecPO(mobile,message,"crm", CrmConstant.YES,"crm"));
                return true;
            }
            return false;
        }
        return false;
    }
}
