package com.shj.eids.controller;

import com.shj.eids.domain.User;
import com.shj.eids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @ClassName: RegisterController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 14:51
 **/
@Controller
public class RegisterController {
    @Autowired
    UserService service;

    @PostMapping("/register/m-register")
    public String register(@RequestParam("email") String email,
                         @RequestParam("password") String password,
                         @RequestParam("captcha") String captcha,
                         HttpSession session){
        String rawCaptcha = (String) session.getAttribute("captcha");
        if(rawCaptcha != null && rawCaptcha.equals(captcha) && email!=null && password != null){
            User u = new User(null, email, password, 1, null);
            try{
                service.userRegister(u);
                session.removeAttribute("captcha");
            }catch (Exception e){
                e.printStackTrace();
            }
            return "login";
        }else {
            return "register";
        }
    }

    @PostMapping("/ttest")
    public String test(){
        return "test";
    }
}