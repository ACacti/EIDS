package com.shj.eids.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: LogoutController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 16:34
 **/
@Controller
public class LogoutController {
    @RequestMapping("/user/m-logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        session.removeAttribute("loginUser");
        Cookie cookie = new Cookie("login", null);
        return "redirect:/";
    }
}