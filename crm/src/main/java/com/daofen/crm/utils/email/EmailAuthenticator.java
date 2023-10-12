package com.daofen.crm.utils.email;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Administrator on 2017/4/8.
 */
public class EmailAuthenticator extends Authenticator {

    private static final String username = "dymgzs@163.com";
    private static final String password = "MUZHI35ZCZ";


    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
