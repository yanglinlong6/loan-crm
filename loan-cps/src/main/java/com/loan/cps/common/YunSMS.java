package com.loan.cps.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YunSMS implements SmsApi {

    private static final String url = "http://120.79.81.207:9090/sms/batch/v1";

    private static final String appkey = "JHbz";

    private static final String appCode = "1000";

    private static final String appSecret = "D0sr4H";

    private static final Logger LOG = LoggerFactory.getLogger(YunSMS.class);

    @Override
    public boolean sendCode(String mobile, String code) {
        return false;
    }

    @Override
    public boolean sendCode2(String mobile, String code, String domain2) {
        String message = getContent(domain2,code);
        return sendMessage(mobile,message);
    }

    @Override
    public boolean sendCode(String mobile, String code, String templateCode) {
        return false;
    }



    @Override
    public boolean sendMessage(String mobile, String message) {

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
                return true;
            }
            return false;
        }
        return false;
    }
//
//    public static void main(String[] args){
//        SmsApi api = new YunSMS();
//        boolean isSuccess = api.sendCode2("13632965527","1111","zxf.bangzheng100.com");
//        System.out.println(isSuccess);
////        System.out.println(api.sendMessage("13632965527","【宁鑫优享】新客户:张球，请及时跟进。"));
//    }
}
