package com.crm.service.email;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2017/4/9.
 */
@Setter
@Getter
public class EmailBO {

    private String subject = "邮件提醒";

    private String message;

    private String emailAddress;

    public EmailBO() {
    }


    public EmailBO(String subject, String message, String emailAddress) {
        this.subject = subject;
        this.message = message;
        this.emailAddress = emailAddress;
    }
}
