package com.daofen.admin.utils.email;

import javax.mail.Session;
import java.util.Properties;

/**
 * Created by Administrator on 2017/4/9.
 */
public class EmailSessionUtil {

    private static Session mailSession = null;

    /**
     * email服务器地址
     */
    public static final String mail_smtp_host = "smtp.163.com";

    /**
     * email服务器端口
     */
    private static final int mail_smtp_port = 25;

    public synchronized static Session getMailSession() {
        if(null == mailSession) {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", mail_smtp_host);
            properties.put("mail.smtp.starttls.enable", "true");//使用 STARTTLS安全连接
            properties.put("mail.smtp.port", 465);             //google使用465或587端口
            properties.put("mail.smtp.auth", "true");        // 使用验证
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            mailSession = Session.getInstance(properties, new EmailAuthenticator());
            return mailSession;
        }
        return mailSession;
    }

//


}
