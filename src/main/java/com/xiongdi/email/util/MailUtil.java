package com.xiongdi.email.util;


import com.xiongdi.email.bean.MailBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class MailUtil {
    @Value("${lance.mail.sender}")
    private String MAIL_SENDER; //邮件发送者

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration configuration; //freemarker

    private Logger logger = LoggerFactory.getLogger(MailUtil.class);

    /**
     * 发送一个简单格式的邮件
     *
     * @param mailBean
     */
    public void sendSimpleMail(MailBean mailBean) throws Exception {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //邮件发送人
        simpleMailMessage.setFrom(MAIL_SENDER);

        //邮件接收人
        simpleMailMessage.setTo(mailBean.getRecipient());
        //邮件主题
        simpleMailMessage.setSubject(mailBean.getSubject());
        //邮件内容
        simpleMailMessage.setText(mailBean.getContent());

        javaMailSender.send(simpleMailMessage);

    }

    /**
     * 发送一个HTML格式的邮件
     *
     * @param mailBean
     */
    public void sendHTMLMail(MailBean mailBean) throws Exception {
        MimeMessage mimeMailMessage = null;

        mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
        mimeMessageHelper.setFrom(MAIL_SENDER);
        mimeMessageHelper.setTo(mailBean.getRecipient());
        mimeMessageHelper.setSubject(mailBean.getSubject());
        mimeMessageHelper.setText(mailBean.getContent(), true);
        javaMailSender.send(mimeMailMessage);

    }

    /**
     * 发送带附件格式的邮件
     *
     * @param mailBean
     */
    public void sendAttachmentMail(MailBean mailBean, File file, String filename) {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText(mailBean.getContent());
            //文件路径
//            FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/image/mail.png"));
            mimeMessageHelper.addAttachment(filename, file);

            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            logger.error("邮件发送失败", e.getMessage());
        }
    }

    /**
     * 发送带静态资源的邮件
     *
     * @param mailBean
     */
    public void sendInlineMail(MailBean mailBean) {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());
            mimeMessageHelper.setText("<html><body>带静态资源的邮件内容，这个一张IDEA配置的照片:<img src='cid:picture' /></body></html>", true);
            //文件路径
            FileSystemResource file = new FileSystemResource(new File("src/main/resources/static/image/mail.png"));
            mimeMessageHelper.addInline("picture", file);

            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            logger.error("邮件发送失败", e.getMessage());
        }
    }

    /**
     * 发送基于Freemarker模板的邮件
     *
     * @param mailBean
     */
    public void sendTemplateMail(MailBean mailBean) {
        MimeMessage mimeMailMessage = null;
        try {
            mimeMailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
            mimeMessageHelper.setFrom(MAIL_SENDER);
            mimeMessageHelper.setTo(mailBean.getRecipient());
            mimeMessageHelper.setSubject(mailBean.getSubject());

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("content", mailBean.getContent());
            model.put("title", "标题Mail中使用了FreeMarker");
            Template template = configuration.getTemplate("mail.ftl");
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            mimeMessageHelper.setText(text, true);

            javaMailSender.send(mimeMailMessage);
        } catch (Exception e) {
            logger.error("邮件发送失败", e.getMessage());
        }

    }

    public void processUpload(HttpServletRequest request) throws IOException {
        String recipient = (String) request.getParameter("recipient");
        String subject = (String) request.getParameter("subject");
        String content = (String) request.getParameter("content");
        MultipartFile file = ((MultipartHttpServletRequest) request).getFile("file");
        String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf("."));
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        File filePath = new File("/usr/liuyang/email/file/");
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        File file1 = new File(filePath, fileName + "." + suffix);
        file.transferTo(file1);
        System.out.println(file.getOriginalFilename());
        sendAttachmentMail(new MailBean(recipient, subject, content), file1, file.getOriginalFilename());
    }
}
