package com.loan.cps.common;

public interface SmsApi {

    String MSG_TEMLATE_G = "【广信】验证码%s，1小时内有效，请勿告诉他人";

    String MSG_TEMLATE_BANG = "【邦正】验证码%s，1小时内有效，请勿告诉他人";

    String MSG_TEMLATE_JUWODAI = "【聚我贷】验证码%s，1小时内有效，请勿告诉他人";

    String MSG_TEMLATE_ZXF = "【左心房】验证码%s，1小时内有效，请勿告诉他人";

    String MSG_TEMLATE_LONG = "【龙信】验证码%s，1小时内有效，请勿告诉他人";

    String MSG_TEMLATE_MOERLONG = "【摩尔龙】验证码%s，1小时内有效，请勿告诉他人";



    boolean sendCode(String mobile,String code);

    boolean sendCode2(String mobile,String code,String domain2);

    boolean sendCode(String mobile,String code,String templateCode);

    boolean sendMessage(String mobile,String message);

    default String getContent(String domain2,String code){
        if(domain2.contains("longloan.cn")){
            return String.format(MSG_TEMLATE_LONG,code);
        }else if(domain2.contains("gzhsdph.com")){
            return String.format(MSG_TEMLATE_G,code);
        }else if(domain2.equals("121.199.39.162") || domain2.contains("moerlong")){
            return String.format(MSG_TEMLATE_MOERLONG,code);
        }else if(domain2.contains("juwodai")){
            return String.format(MSG_TEMLATE_JUWODAI,code);
        }else if(domain2.contains("fund.bangzheng100.com") || domain2.contains("zxf") || domain2.contains("zuoxinfang") || domain2.contains("zxfqf.com")){
           return String.format(MSG_TEMLATE_ZXF,code);
        }else{
            return String.format(MSG_TEMLATE_BANG,code);
        }
    }

}
