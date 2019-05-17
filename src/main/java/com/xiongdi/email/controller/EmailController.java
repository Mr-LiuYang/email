package com.xiongdi.email.controller;

import com.xiongdi.email.bean.MailBean;
import com.xiongdi.email.bean.Result;
import com.xiongdi.email.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(value = "/email")
@RestController
public class EmailController {
    private Logger logger = LoggerFactory.getLogger(EmailController.class);
    @Autowired
    private MailUtil mailUtil;

    @RequestMapping("/hello")
    public String sendSimpleMail1() throws Exception {
        return "hello";
    }

    @RequestMapping("/text")
    public Result sendSimpleMail(String recipient, String subject, String content) {
        Result result = null;
        try {
            MailBean mailBean = createMailBean(recipient, subject, content);
            mailUtil.sendSimpleMail(mailBean);
            logger(recipient, subject, content, null);
            result = new Result("1", "success");
        } catch (Exception e) {
            logger(recipient, subject, content, e.getMessage());
            result = new Result("2", e.getMessage());
        } finally {
            return result;
        }
    }

    @RequestMapping("/html")
    public Result sendHTMLMail(String recipient, String subject, String content) {
        Result result = null;
        try {
            MailBean mailBean = createMailBean(recipient, subject, content);
            mailUtil.sendHTMLMail(mailBean);
            logger(recipient, subject, content, null);
            result = new Result("1", "success");
        } catch (Exception e) {
            logger(recipient, subject, content, e.getMessage());
            result = new Result("2", e.getMessage());
        } finally {
            return result;
        }
    }

    @RequestMapping("/attachment")
    public void sendAttachmentMail(HttpServletRequest request) throws Exception {
        mailUtil.processUpload(request);
    }


    @RequestMapping("/template")
    public void sendTemplateMail() throws Exception {
        MailBean mailBean = new MailBean();
        mailBean.setRecipient("15079150591@163.com");
        mailBean.setSubject("测试SpringBoot发送带FreeMarker邮件");
        mailBean.setContent("希望大家能够学到自己想要的东西，谢谢各位的帮助！！！");
        mailUtil.sendTemplateMail(mailBean);
    }

    private void logger(String recipient, String subject, String content, String execption) {
        if (execption == null) {
            logger.info("recipient :" + recipient + "\n" + "subject:" + subject + "\n" + "content:" + content + "\n" + "execption:" + execption);
        } else {
            logger.info("recipient :" + recipient + "\n" + "subject:" + subject + "\n" + "content:" + content);
        }
    }

    private MailBean createMailBean(String recipient, String subject, String content) {
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(recipient);
        mailBean.setSubject(subject);
        mailBean.setContent(content);
        return mailBean;
    }
}
