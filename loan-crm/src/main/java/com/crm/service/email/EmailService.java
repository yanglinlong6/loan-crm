package com.crm.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/4/9.
 */
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * email服务器地址 smtp.exmail.qq.com
     */
    private static final String mail_smtp_host = "smtp.qq.com";

    /**邮箱协议*/
    private static final String protocol = "smtp";
    private static final String message_type = "text/html;charset=utf-8";

    /** 邮件发送者 */
    private static final String from = "509124739@qq.com";

    /** 邮件发送密码  */
    private static final String password = "jzdecshowgtzbieh";

    /** 邮件发送者 */
    private static final String from2 = "814481025@qq.com";

    /** 邮件发送密码  */
    private static final String password2 = "angqisspkxjlbfhg";

    private static final String fromAli = "service@bangzheng100.com";
    private static final String passwordAli = "008901Zh";

    private static Transport transport;

    @SuppressWarnings("static-access")
    public static void sendMessage(EmailBO email) throws MessagingException {
        Session session = EmailSessionUtil.getAliSession(fromAli,passwordAli);
        MimeMessage message = createMimeMessage(email,session,fromAli);
        // 第三步：发送消息
        Transport transport = session.getTransport(protocol);
        try{
            transport.connect(EmailSessionUtil.mail_smtp_host_ali, fromAli, passwordAli);
        }catch (Exception e){
            log.error("发送邮件异常：{}",email.getSubject(),e);
            session = EmailSessionUtil.getQQSession(from2,password2);
            message = createMimeMessage(email,session,from2);
            transport = session.getTransport(protocol);
            transport.connect(EmailSessionUtil.mail_smtp_host, from2,password2);
        }
        transport.send(message, message.getRecipients(Message.RecipientType.TO));
        if(log.isDebugEnabled()){
            log.debug("email message send is yes：{}",email.getSubject());
        }
        transport.close();
    }

    private static MimeMessage createMimeMessage(EmailBO email, Session mailSession, String fromMail) throws MessagingException {
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(fromMail));
        String[] addressArray = email.getEmailAddress().split(",");
        InternetAddress[] targets = new InternetAddress[addressArray.length];
        for(int i = 0; i < addressArray.length; i++) {
            targets[i] = new InternetAddress(addressArray[i]);
        }
        message.addRecipients(Message.RecipientType.TO, targets);
        message.setSentDate(Calendar.getInstance().getTime());
        message.setSubject(email.getSubject());
        message.setContent(email.getMessage(), message_type);
        return message;
    }
}
