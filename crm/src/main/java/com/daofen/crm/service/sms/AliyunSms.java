package com.daofen.crm.service.sms;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.service.sms.dao.SmsSendRecordMapper;
import com.daofen.crm.service.sms.model.SmsSendRecordPO;
import com.daofen.crm.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AliyunSms implements  SmsApi{

    private static final Logger LOG = LoggerFactory.getLogger(AliyunSms.class);

    @Override
    public boolean sendCode(String mobile,String code) {

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G36FQT6fcdJtZUWffPh", "sKu5BN81GjLSlnulXm5jF0rvUejp1K");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "繁鑫数科");
        request.putQueryParameter("TemplateCode", "SMS_213087508");
        JSONObject data = new JSONObject();
        data.put("code",code);
        request.putQueryParameter("TemplateParam", data.toJSONString());
        try {
            //{"Message":"OK","RequestId":"4D611167-3D75-4F34-827B-5643C4555E48","BizId":"993211879166052682^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            LOG.info("阿里云短信服务发送:{}-{},发送结果:{}",mobile,code,JSONUtil.toString(response.getData()));
            JSONObject responseJson = JSONUtil.toJSON(response.getData());
            if("OK".endsWith(responseJson.getString("Code"))){
                SmsSendRecordPO record = new SmsSendRecordPO(mobile,code);
                AppContextUtil.getBean(SmsSendRecordMapper.class).insertSmsSendRecord(record);
                return true;
            }
            return false;
        } catch (ServerException e) {
            LOG.error("阿里云短信服务发送异常:ServerException:{}",e.getErrMsg(),e);
            return false;
        } catch (ClientException e) {
            LOG.error("阿里云短信服务发送异常:ClientException:{}",e.getErrMsg(),e);
            return false;
        } catch(Exception e){
            LOG.error("阿里云短信服务发送异常:Exception:{}",e.getMessage(),e);
            return false;
        }
    }

    @Override
    public boolean sendCode(String mobile, String code, String templateCode) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G36FQT6fcdJtZUWffPh", "sKu5BN81GjLSlnulXm5jF0rvUejp1K");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "繁鑫数科");
        request.putQueryParameter("TemplateCode", "SMS_213087508");
        JSONObject data = new JSONObject();
        data.put("code",code);
        request.putQueryParameter("TemplateParam", data.toJSONString());
        try {
            //{"Message":"OK","RequestId":"4D611167-3D75-4F34-827B-5643C4555E48","BizId":"993211879166052682^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            LOG.info("阿里云短信服务发送:{}-{},发送结果:{}",mobile,code,JSONUtil.toString(response.getData()));
            JSONObject responseJson = JSONUtil.toJSON(response.getData());
            if("OK".endsWith(responseJson.getString("Code"))){
                SmsSendRecordPO record = new SmsSendRecordPO(mobile,code);
                AppContextUtil.getBean(SmsSendRecordMapper.class).insertSmsSendRecord(record);
                return true;
            }
            return false;
        } catch (ServerException e) {
            LOG.error(e.getErrMsg(),e);
            return false;
        } catch (ClientException e) {
            LOG.error(e.getErrMsg(),e);
            return false;
        } catch(Exception e){
            LOG.error(e.getMessage(),e);
            return false;
        }
    }


    @Override
    public boolean sendMessage(String mobile,String message) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G36FQT6fcdJtZUWffPh", "sKu5BN81GjLSlnulXm5jF0rvUejp1K");
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "繁鑫数科");
        request.putQueryParameter("TemplateCode", "SMS_213087508");
        JSONObject data = new JSONObject();
        data.put("name",message);
        request.putQueryParameter("TemplateParam", data.toJSONString());
        try {
            //{"Message":"OK","RequestId":"4D611167-3D75-4F34-827B-5643C4555E48","BizId":"993211879166052682^0","Code":"OK"}
            CommonResponse response = client.getCommonResponse(request);
            LOG.info("阿里云短信服务发送:{}-{},发送结果:{}",mobile,message,JSONUtil.toString(response.getData()));
            JSONObject responseJson = JSONUtil.toJSON(response.getData());
            if("OK".endsWith(responseJson.getString("Code"))){
                SmsSendRecordPO record = new SmsSendRecordPO(mobile,message);
                AppContextUtil.getBean(SmsSendRecordMapper.class).insertSmsSendRecord(record);
                return true;
            }
            return false;
        } catch (ServerException e) {
            LOG.error(e.getErrMsg(),e);
            return false;
        } catch (ClientException e) {
            LOG.error(e.getErrMsg(),e);
            return false;
        } catch(Exception e){
            LOG.error(e.getMessage(),e);
            return false;
        }
    }


    public static void main(String[] args){
        AliyunSms sms = new AliyunSms();
        sms.sendCode("13632965527","123456");
    }
}
