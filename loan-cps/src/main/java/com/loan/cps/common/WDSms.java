package com.loan.cps.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WDSms implements SmsApi {

    private static final Logger LOG = LoggerFactory.getLogger(WDSms.class);

    private static final String username = "NBBZWY";

    private static final String password = "BZWY098";

    private static final String url = "http://116.62.162.203:8001/sms/api/sendMessageOne";

    private static final String MSG_TEMLATE_G = "【广信】验证码%s，1小时内有效，请勿告诉他人";

    private static final String MSG_TEMLATE_BANG = "【邦正】验证码%s，1小时内有效，请勿告诉他人";

    private static final String MSG_TEMLATE_JUWODAI = "【聚我贷】验证码%s，1小时内有效，请勿告诉他人";

    private static final String MSG_TEMLATE_ZXF = "【左心房】验证码%s，1小时内有效，请勿告诉他人";

    private static final String MSG_TEMLATE_LONG = "【龙信】验证码%s，1小时内有效，请勿告诉他人";

    private static final String MSG_TEMLATE_MOERLONG = "【摩尔龙】验证码%s，1小时内有效，请勿告诉他人";

    private static final String MSG_TEMLATE_JAR = "【甲耀法务】验证码%s，1小时内有效，请勿告诉他人";

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

        msg.put("content",String.format(MSG_TEMLATE_BANG,code));

        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,code,result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            return true;
        }
        return false;
    }

    private static final String DOMAIN_SMS_TEMPLATE = "domain_sms_template";

    @Override
    public boolean sendCode2(String mobile, String code, String domain2) {
        if(StringUtils.isBlank(domain2)){
            return sendCode(mobile,code);
        }

        JSONArray msgArray = new JSONArray();
        JSONObject msg = new JSONObject();
        msg.put("phone",mobile);
        msg.put("content",getContent(domain2,code));
//        if(domain2.contains("longloan.cn")){
//            msg.put("content",String.format(MSG_TEMLATE_LONG,code));
//        }else if(domain2.contains("gzhsdph.com")){
//            msg.put("content",String.format(MSG_TEMLATE_G,code));
//        }else if(domain2.contains("jia.bangzheng100.com")){
//            msg.put("content",String.format(MSG_TEMLATE_JAR,code));
//        }else if(domain2.equals("121.199.39.162") || domain2.contains("moerlong")){
//            msg.put("content",String.format(MSG_TEMLATE_MOERLONG,code));
//        }else if(domain2.contains("juwodai")){
//            msg.put("content",String.format(MSG_TEMLATE_JUWODAI,code));
//        }else if(domain2.contains("fund.bangzheng100.com") || domain2.contains("zxf") || domain2.contains("zuoxinfang") || domain2.contains("zxfqf.com")){
//            msg.put("content",String.format(MSG_TEMLATE_ZXF,code));
//        }else{
//            msg.put("content",String.format(MSG_TEMLATE_BANG,code));
//        }
        msgArray.add(msg);

        long timestamp = System.currentTimeMillis();
        JSONObject data = new JSONObject();
        data.put("userName",username);
        data.put("timestamp",timestamp);
        data.put("sign",MD5Util.getMd5String(username+timestamp+MD5Util.getMd5String(password)));
        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,code,result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
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
        msg.put("content",String.format(MSG_TEMLATE_BANG,content));

        JSONArray msgArray = new JSONArray();
        msgArray.add(msg);

        data.put("messageList",msgArray);
        // {"code":0,"message":"处理成功","data":[{"code":0,"message":"处理成功","msgId":2899758,"phone":"13632965527"}]}
        String result = HttpUtil.postForObject(url,data);
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,code,result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(String mobile, String message) {
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
        LOG.info("【沃动科技】{}-{}短信发送结果：{}",mobile,message,result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            return true;
        }
        return false;
    }

//    public static void main(String[] args){
//        SmsApi api = new WDSms();
//        api.sendCode("13480678716","6666");
//    }
}
