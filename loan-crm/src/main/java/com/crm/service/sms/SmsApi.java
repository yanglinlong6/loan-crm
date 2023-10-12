package com.crm.service.sms;

public interface SmsApi {

    String MSG_TEMPLATE_ADMIN = "接收到新客户{%s}请及时跟进";

    String MESSAGE_TEMPLATE_DIS = "【%s】您有新客户{%s}请及时跟进";

    String MESSAGE_TEMPLATE_CUSTOMER = "【%s】尊敬的%s，已经收到您的申请，稍后请注意接听客户经理来电！";

    boolean sendCode(String mobile,String code);

    boolean sendCode2(String mobile,String code,String domain2);

    boolean sendCode(String mobile,String code,String templateCode);

    boolean sendMessage(String mobile,String message);

    boolean sendMessage(String mobile,String message,String createBy);

}
