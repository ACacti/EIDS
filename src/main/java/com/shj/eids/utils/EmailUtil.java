package com.shj.eids.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @ClassName: EmailUtil
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 15:24
 **/
@Component
public class EmailUtil {
    @Autowired
    private JavaMailSenderImpl mailSender;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static String from = "shangjinv6@163.com";
    public void sendSimpleEmail(String text, String to, String subject){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailMessage.setTo(to);
        mailSender.send(mailMessage);
        logger.info("发送邮件，主题："+ subject + "\n接受者：" + to + "\n内容：" + text );

    }
    public void sendComplexEmail(String text, String to, String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        mailSender.send(mimeMessage);
        logger.info("发送邮件，主题："+ subject + "\n接受者：" + to + "\n内容：" + text );
    }


    @Async
    public void sendComplexEmailByAsynchronousMode(String text, String to, String subject) throws MessagingException {
        sendComplexEmail(text, to, subject);
    }
}