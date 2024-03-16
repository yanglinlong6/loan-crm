package com.help.loan.distribute.service.sms;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.help.loan.distribute.common.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class AliyunSms implements SmsApi {

    private static final Logger LOG = LoggerFactory.getLogger(AliyunSms.class);

    @Override
    public boolean sendCode(String mobile, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tEH5YWQhKMsqmEgyfD6", "DA5IKZYPDSKT2dDccbKaqGz3xJWM8Z");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "宁波伟业");
        request.putQueryParameter("TemplateCode", "SMS_213855158");
        JSONObject data = new JSONObject();
        data.put("code", code);
        request.putQueryParameter("TemplateParam", data.toJSONString());
        try {
            //{"Message":"OK","RequestId":"4D611167-3D75-4F34-827B-5643C4555E48","BizId":"993211879166052682^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            LOG.info("阿里云短信服务发送:{}-{},发送结果:{}", mobile, code, JSONUtil.toJsonString(response.getData()));
            JSONObject responseJson = JSONUtil.toJSON(response.getData());
            if ("OK".endsWith(responseJson.getString("Code"))) {
                return true;
            }
            return false;
        } catch (ServerException e) {
            LOG.error(e.getErrMsg(), e);
            return false;
        } catch (ClientException e) {
            LOG.error(e.getErrMsg(), e);
            return false;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendCode(String mobile, String code, String templateCode) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tEH5YWQhKMsqmEgyfD6", "DA5IKZYPDSKT2dDccbKaqGz3xJWM8Z");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "宁波伟业");
        request.putQueryParameter("TemplateCode", "SMS_213855158");
        JSONObject data = new JSONObject();
        data.put("code", code);
        request.putQueryParameter("TemplateParam", data.toJSONString());
        try {
            //{"Message":"OK","RequestId":"4D611167-3D75-4F34-827B-5643C4555E48","BizId":"993211879166052682^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            LOG.info("阿里云短信服务发送:{}-{},发送结果:{}", mobile, code, JSONUtil.toJsonString(response.getData()));
            JSONObject responseJson = JSONUtil.toJSON(response.getData());
            if ("OK".endsWith(responseJson.getString("Code"))) {
                return true;
            }
            return false;
        } catch (ServerException e) {
            LOG.error(e.getErrMsg(), e);
            return false;
        } catch (ClientException e) {
            LOG.error(e.getErrMsg(), e);
            return false;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public boolean sendMessage(String mobile, String date, BigDecimal amount, String remark) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5tEH5YWQhKMsqmEgyfD6", "DA5IKZYPDSKT2dDccbKaqGz3xJWM8Z");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "宁波伟业");
        request.putQueryParameter("TemplateCode", "SMS_213855158");
        JSONObject data = new JSONObject();
        data.put("date", date);
        data.put("amount", amount);
        data.put("order", remark);
        request.putQueryParameter("TemplateParam", data.toJSONString());
        try {
            //{"Message":"OK","RequestId":"4D611167-3D75-4F34-827B-5643C4555E48","BizId":"993211879166052682^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            LOG.debug("阿里云短信服务发送:{}-{},发送结果:{}", mobile, data, JSONUtil.toJsonString(response.getData()));
            JSONObject responseJson = JSONUtil.toJSON(response.getData());
            if ("OK".endsWith(responseJson.getString("Code"))) {
                return true;
            }
            return false;
        } catch (ServerException e) {
            LOG.error(e.getErrMsg(), e);
            return false;
        } catch (ClientException e) {
            LOG.error(e.getErrMsg(), e);
            return false;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public static void main(String[] args) {
        SmsApi smsApi = new AliyunSms();
        smsApi.sendCode("13086716076", "1245");
    }

}
