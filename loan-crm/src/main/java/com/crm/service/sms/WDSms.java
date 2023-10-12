package com.crm.service.sms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.service.sms.model.MsgRecPO;
import com.crm.util.AppContextUtil;
import com.crm.util.HttpUtil;
import com.crm.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class WDSms implements SmsApi {

    private static final Logger LOG = LoggerFactory.getLogger(WDSms.class);

    private static final String username = "NBBZWY";

    private static final String password = "BZWY098";

    private static final String url = "http://116.62.162.203:8001/sms/api/sendMessageOne";

    private static final String TEMLATE_FX = "【德务发顾】验证码%s，1小时内有效，请勿告诉他人";

    private static final String TEMLATE_BN = "【博能】验证码%s，1小时内有效，请勿告诉他人";

    private static final String TEMLATE_XK = "【小康】验证码%s，1小时内有效，请勿告诉他人";

    @Override
    public boolean sendCode(String mobile, String code) {

        long timestamp = System.currentTimeMillis();
        String sign = MD5Util.getMd5String(username+timestamp+MD5Util.getMd5String(password));

        JSONObject data = new JSONObject();
        data.put("userName",username);
        data.put("timestamp",timestamp);
        data.put("sign",sign);

        JSONObject msg = new JSONObject();
        msg.put("phone",mobile);
        msg.put("content",String.format(TEMLATE_FX,code));

        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,code,result);
        if(0 == JSONObject.parseObject(result).getIntValue("code")){
            return true;
        }
        return false;
    }

    private static final String DOMAIN_SMS_TEMPLATE = "domain_sms_template";

    @Override
    public boolean sendCode2(String mobile, String code, String domain2) {
        long timestamp = System.currentTimeMillis();
        String sign = MD5Util.getMd5String(username+timestamp+MD5Util.getMd5String(password));

        JSONObject data = new JSONObject();
        data.put("userName",username);
        data.put("timestamp",timestamp);
        data.put("sign",sign);

        JSONObject msg = new JSONObject();
        msg.put("phone",mobile);
        if(StringUtils.isBlank(domain2)){
            msg.put("content",String.format(TEMLATE_FX,code));
        }else {
            if(domain2.contains("xiaokang"))
                msg.put("content",String.format(TEMLATE_XK,code));
            else
                msg.put("content",String.format(TEMLATE_BN,code));
        }


        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,code,result);
        if(0 == JSONObject.parseObject(result).getIntValue("code")){
            return true;
        }
        return false;
    }

    @Override
    public boolean sendCode(String mobile, String code, String content) {
        long timestamp = System.currentTimeMillis();
        String sign = MD5Util.getMd5String(username+timestamp+MD5Util.getMd5String(password));

        JSONObject data = new JSONObject();
        data.put("userName",username);
        data.put("timestamp",timestamp);
        data.put("sign",sign);

        JSONObject msg = new JSONObject();
        msg.put("phone",mobile);
        msg.put("content",String.format(TEMLATE_FX,content));

        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,code,result);
        if(0 == JSONObject.parseObject(result).getIntValue("code")){
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String mobile, String message) {

        if(StringUtils.isBlank(mobile) || StringUtils.isBlank(message)){
            LOG.info("【沃动科技】发送消息失败：手机号码：{}-{}",mobile,message);
            return false;
        }

        long timestamp = System.currentTimeMillis();
        String sign = MD5Util.getMd5String(username+timestamp+MD5Util.getMd5String(password));
        JSONObject data = new JSONObject();
        data.put("userName",username);
        data.put("timestamp",timestamp);
        data.put("sign",sign);

        JSONObject msg = new JSONObject();
        msg.put("phone",mobile);
        msg.put("content",message);

        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{},短信发送结果：{}",mobile,message,result);
        if(CrmConstant.INIT == JSONObject.parseObject(result).getByteValue("code")){
            ApplicationContext context = AppContextUtil.get();
            if(null != context){
                SmsService  smsService = context.getBean("smsServiceImpl",SmsService.class);
                if(null != smsService)
                    smsService.addMsgRec(new MsgRecPO(mobile,message,"crm",CrmConstant.YES,"crm"));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String mobile, String message, String createBy) {
        if(StringUtils.isBlank(mobile) || StringUtils.isBlank(message)){
            LOG.info("【沃动科技】发送消息失败：手机号码：{}-{}",mobile,message);
            return false;
        }

        long timestamp = System.currentTimeMillis();
        String sign = MD5Util.getMd5String(username+timestamp+MD5Util.getMd5String(password));
        JSONObject data = new JSONObject();
        data.put("userName",username);
        data.put("timestamp",timestamp);
        data.put("sign",sign);

        JSONObject msg = new JSONObject();
        msg.put("phone",mobile);
        msg.put("content",message);

        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{},短信发送结果：{}",mobile,message,result);
        if(CrmConstant.INIT == JSONObject.parseObject(result).getByteValue("code")){
            ApplicationContext context = AppContextUtil.get();
            if(null != context){
                SmsService  smsService = context.getBean("smsServiceImpl",SmsService.class);
                if(null != smsService)
                    smsService.addMsgRec(new MsgRecPO(mobile,message,"crm",CrmConstant.YES,"crm"));
            }
            return true;
        }
        return false;
    }

}
