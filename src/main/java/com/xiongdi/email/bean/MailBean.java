package com.xiongdi.email.bean;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.io.Serializable;


@Data
public class MailBean implements Serializable {
    private String recipient;   //邮件接收人
    private String subject; //邮件主题
    private String content; //邮件内容

    public MailBean() {
    }

    public MailBean(String recipient, String subject, String content) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }
}
