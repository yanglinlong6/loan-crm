package com.daofen.crm.service.sms;

public interface SmsApi {

    boolean sendCode(String mobile,String code);

    boolean sendCode(String mobile,String code,String templateCode);

    boolean sendMessage(String mobile,String message);

}
