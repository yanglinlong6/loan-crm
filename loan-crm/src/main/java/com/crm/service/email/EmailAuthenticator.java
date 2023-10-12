package com.crm.service.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Administrator on 2017/4/8.
 */
public class EmailAuthenticator extends Authenticator {

    private String username;
    private String password;

    public EmailAuthenticator(String username,String password){
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }


}
