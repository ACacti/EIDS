package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.User;
import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import com.shj.eids.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: LoginRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 14:46
 **/
@RestController
public class LoginRestController {
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    EmailUtil emailUtil;

    @PostMapping("/login/m-identify")
    public String loginIdentify(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("isAdmin") Boolean isAdmin){
        boolean result = false;
        if(isAdmin){
            result = adminService.loginIdentify(email, password);
        }else {
            result = userService.loginIdentify(email, password);
        }
        Map<String, Object> res = new HashMap<>();
        if(result){
            res.put("identification", true);
        }else{
            res.put("identification", false);
            res.put("msg", "邮箱未注册或密码错误");
        }
        return JSON.toJSONString(res);
    }

    @PostMapping("/login/m-sendCaptcha")
    public String sendCaptcha(@RequestParam("email") String email,
                              HttpSession session){
        Map<String, String> map = new HashMap<>();
        if(!userService.hasEmail(email)){
            map.put("result", "error");
            map.put("msg", "邮箱未注册");
            return JSON.toJSONString(map);
        }
        String captcha = defaultKaptcha.createText();
        String subject = "EDIS|修改密码";
        String text = "欢迎使用疫情信息发布系统，您的验证码为:<strong>" + captcha + "</strong><br/>如果您未申请注册本系统，请忽略此邮件。";

        try{
            emailUtil.sendComplexEmail(text, email, subject);
            session.setAttribute("modifyPassowrdCaptcha", captcha);
            map.put("result", "success");
            map.put("modifyPassowrdCaptcha",captcha);
            return JSON.toJSONString(map);
        }catch (Exception e){
            map.put("result", "error");
            map.put("msg", e.getMessage());
            return JSON.toJSONString(map);
        }
    }

    @PostMapping("/login/m-modifyPass")
    public String modifyPass(@RequestParam("email") String email, @RequestParam("newPass") String newPass){
        Map<String, String> map = new HashMap<>();
        try {
            User u  = userService.getUserByEmail(email);
            if(u != null){
                u.setEmail(email);
                u.setPassword(newPass);
                userService.updateUser(u);
            }else{
                Admin admin = adminService.getAdminByEmail(email);
                admin.setPassword(newPass);
                adminService.update(admin);
            }
            map.put("result", "success");
            return JSON.toJSONString(map);
        }catch (Exception e){
            e.printStackTrace();
            map.put("result", "error");
            map.put("msg", "邮箱未注册");
            return JSON.toJSONString(map);
        }
    }
}