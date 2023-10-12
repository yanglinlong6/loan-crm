package com.daofen.admin.utils.email;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/4/9.
 */
public class EmailService {


    /**
     * email服务器地址
     */
    @SuppressWarnings("unused")
    private static final String mail_smtp_host = "smtp.163.com";

    /**
     * email服务器端口
     */
    @SuppressWarnings("unused")
//	private static  final int mail_smtp_port = 25;

    /**邮箱协议*/
    private static final String protocol = "smtp";

    /**
     * 邮件发送者
     */
    private static final String from = "dymgzs@163.com";

//    private static  InternetAddress[] targets = new InternetAddress[2];

    /**
     * 邮件发送密码
     */
    private static final String password = "MUZHI35ZCZ";

    private static final String message_type = "text/html;charset=utf-8";


    @SuppressWarnings("static-access")
    public static void sendMessage(EmailBO email) throws javax.mail.MessagingException {
        Session mailSession = EmailSessionUtil.getMailSession();
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(from));
        String[] addressArray = email.getEmailAddress().split(",");
        InternetAddress[] targets = new InternetAddress[addressArray.length];
        for(int i = 0; i < addressArray.length; i++) {
            targets[i] = new InternetAddress(addressArray[i]);
        }
        message.addRecipients(Message.RecipientType.TO, targets);
        message.setSentDate(Calendar.getInstance().getTime());
        message.setSubject(email.getSubject());
        message.setContent(email.getMessage(), message_type);

        // 第三步：发送消息
        Transport transport = mailSession.getTransport(protocol);
        transport.connect(EmailSessionUtil.mail_smtp_host, from, password);
        transport.send(message, message.getRecipients(Message.RecipientType.TO));
        System.out.println("message yes");
        transport.close();
    }

//	public static void sendMessage(EmailBO email,String msg) throws  javax.mail.MessagingException {
//        Session mailSession = EmailSessionUtil.getMailSession();
//        MimeMessage message = new MimeMessage(mailSession);
//        message.setFrom( new InternetAddress(from));
//        targets[0] = new InternetAddress("rolin@klpdigitech.com");
//        targets[1] = new InternetAddress("info@klpdigitech.com");
//        message.addRecipients(Message.RecipientType.TO,targets);
//        message.setSentDate(Calendar.getInstance().getTime());
//        message.setSubject(email.getSubject());
//        message.setContent(msg, "text/html;charset=UTF-8");
//
//        // 第三步：发送消息
//        Transport transport = mailSession.getTransport(protocol);
//        transport.connect(EmailSessionUtil.mail_smtp_host,from, password);
//        transport.send(message, message.getRecipients(Message.RecipientType.TO));
//        System.out.println("message yes");
//    }
}
