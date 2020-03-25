package com.shj.eids.controller;

import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;

    @PostMapping("/login/m-login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam(value = "remember", required = false) String remember,
                        @RequestParam(value = "isAdmin", required = false) String isAdmin,
                        HttpSession session,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model){
        boolean result;
        boolean isAdmin1 = (isAdmin!=null);
        boolean remember1 = (remember!=null);
        logger.debug("登录身份是否为管理员：" + isAdmin1);
        logger.debug("是否要记住密码：" + remember1);
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
                Cookie cookie = new Cookie("login", (isAdmin1 ? "admin":"user") + "-" + email+ "-" + password);
                cookie.setMaxAge(60*60*24*7);
                cookie.setPath(session.getServletContext().getContextPath());
                response.addCookie(cookie);
            }
            logger.debug("登录成功！");
            if(isAdmin1){
                session.setAttribute("isAdmin", true);
                return "redirect:/admin";
            }
            session.setAttribute("isAdmin", false);
            return "redirect:/index";
        }else{
            logger.debug("登录失败！");
            model.addAttribute("msg", "邮箱未注册或密码错误.");
            return "login";
        }
    }
}