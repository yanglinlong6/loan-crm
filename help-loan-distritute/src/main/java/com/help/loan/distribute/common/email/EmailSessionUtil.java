package com.help.loan.distribute.common.email;

import javax.mail.Session;
import java.util.Properties;

/**
 * Created by Administrator on 2017/4/9.
 */
public class EmailSessionUtil {

    private static Session qqSession = null;

    /**
     * email服务器地址 smtp.qq.com   smtp.exmail.qq.com
     */
    public static final String mail_smtp_host = "smtp.qq.com";

    public static final String mail_smtp_host_ali = "smtp.mxhichina.com";

    /**
     * email服务器端口
     */
//    private static final int mail_smtp_port = 25;

    private static Session aliSession = null;

    public synchronized static Session getAliSession(String username,String password) {
        if(null == aliSession) {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", mail_smtp_host_ali);
            properties.put("mail.smtp.starttls.enable", "true");//使用 STARTTLS安全连接
            properties.put("mail.smtp.port", 465);             //google使用465或587端口  qq:587  腾讯企业邮箱：465
            properties.put("mail.smtp.auth", "true");        // 使用验证
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            aliSession = Session.getInstance(properties, new EmailAuthenticator(username,password));
            return aliSession;
        }
        return aliSession;
    }

    public synchronized static Session getQQSession(String username, String password) {
        if(null == qqSession) {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", mail_smtp_host);
            properties.put("mail.smtp.starttls.enable", "true");//使用 STARTTLS安全连接
            properties.put("mail.smtp.port", 587);             //google使用465或587端口  qq:587  腾讯企业邮箱：465
            properties.put("mail.smtp.auth", "true");        // 使用验证
            properties.put("mail.transport.protocol", "smtp");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            qqSession = Session.getInstance(properties, new EmailAuthenticator(username,password));
            return qqSession;
        }
        return qqSession;
    }

//    public synchronized static Session getNewMailSession(String username,String password) {
//        Properties properties = new Properties();
//        properties.put("mail.smtp.host", mail_smtp_host);
//        properties.put("mail.smtp.starttls.enable", "true");//使用 STARTTLS安全连接
//        properties.put("mail.smtp.port", 587);             //google使用465或587端口  qq:587  腾讯企业邮箱：465
//        properties.put("mail.smtp.auth", "true");        // 使用验证
//        properties.put("mail.transport.protocol", "smtp");
//        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        mailSession = Session.getInstance(properties, new EmailAuthenticator(username,password));
//        return mailSession;
//    }



}
