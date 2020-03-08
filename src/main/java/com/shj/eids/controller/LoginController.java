package com.shj.eids.controller;

import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: UserController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 12:06
 **/
@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;
    @PostMapping("/login/m-login")
    @ResponseBody
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam("remember") String[] remember,
                        @RequestParam("isAdmin") String[] isAdmin,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model){
        boolean result;
        boolean isAdmin1 = "true".equals(isAdmin[0]);
        boolean remember1 = "true".equals(remember[0]);
        Object account = null;
        if(isAdmin1){
            result = adminService.loginIdentify(email, password);
            account = result ? adminService.getAdminByEmail(email):null;

        }else {
            result = userService.loginIdentify(email, password);
            account = result ? userService.getUserByEmail(email): null;
        }
        if(account != null){
            //验证成功,执行登录操作
            session.setAttribute("loginAccount", account);
            if(remember1){
                Cookie cookie = new Cookie("login", email + "-" + password);
                cookie.setMaxAge(60*60*24*7);
                response.addCookie(new Cookie("login", (isAdmin1 ? "admin":"user") + "-" + email+ "-" + password));
            }
            return "index";
        }else{
            model.addAttribute("msg", "邮箱未注册或密码错误.");
            return "login";
        }
    }
}