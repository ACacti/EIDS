package com.shj.eids;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@SpringBootTest
class EidsApplicationTests {
    @Autowired
    JavaMailSenderImpl mailSender;
//    @Test
        //复杂的邮件
    void complexMail() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        //组装
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);//true: 开启多文件
        mimeMessageHelper.setSubject("复杂邮件测试");
        mimeMessageHelper.setText("<h1>这是一封复杂邮件</h1>", true);//true: 支持html内容
        mimeMessageHelper.setTo("1694634080@qq.com");
        mimeMessageHelper.setFrom("shangjinv6@163.com");
        mailSender.send(message);

    }

}
