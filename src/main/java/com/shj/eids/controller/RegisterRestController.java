package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shj.eids.service.UserService;
import com.shj.eids.utils.EmailUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Provider;

/**
 * @ClassName: RegisterRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 14:52
 **/
@RestController
public class RegisterRestController {
    @Autowired
    UserService userService;
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    EmailUtil emailUtil;
    @PostMapping("/register/m-hasEmail")
    public String hasEmail(@RequestParam("email") String email){
        return JSON.toJSONString(userService.hasEmail(email));
    }

    @PostMapping("/register/m-sendCaptcha")
    public String sendCaptcha(HttpSession session, @RequestParam("email") String email) throws MessagingException {
        String captcha = defaultKaptcha.createText();
        String subject = "疫情信息发布系统注册";
        String text = "欢迎注册疫情信息发布系统，您的验证码为:<strong>" + captcha + "</strong><br/>如果您未申请注册本系统，请忽略此邮件。";
        emailUtil.sendComplexEmail(text, email, subject);
        session.setAttribute("captcha", captcha);
        return "true";
    }

    @PostMapping("/register/m-getCaptcha")
    public String getCaptcha(HttpSession session, @Param("captcha") String captcha){
        String rawcaptcha = (String) session.getAttribute("captcha");
        if(rawcaptcha==null){
            return "false";
        }
        return rawcaptcha.equals(captcha) ? "true":"false";
    }
}