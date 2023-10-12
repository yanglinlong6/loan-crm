package com.help.loan.distribute.service.sms;

import java.math.BigDecimal;

public interface SmsApi {

    boolean sendCode(String mobile, String code);

    boolean sendCode(String mobile, String code, String templateCode);

    boolean sendMessage(String mobile, String date, BigDecimal amount, String remark);

}
