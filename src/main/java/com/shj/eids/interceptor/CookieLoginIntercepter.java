package com.shj.eids.interceptor;

import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: CookieLoginIntercepter
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-22 11:49
 **/
@Component
public class CookieLoginIntercepter implements HandlerInterceptor {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("Cookie登录检查拦截器启动");
        HttpSession session = request.getSession();
        if(session.getAttribute("loginAccount") != null){
            logger.debug("session已经登录，无需检查cookie");
        }else {
            Cookie[] cookies = request.getCookies();
            String[] info = null;
            logger.debug("查询本地Cookies");
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("login")) {
                    info = cookie.getValue().split("-");
                    break;
                }
            }
            if (info != null && info.length >= 3) {
                //本地存有登录信息
                if ("admin".equals(info[0])) {
                    //管理员登录信息，验证密码
                    if (adminService.loginIdentify(info[1], info[2])) {
                        session.setAttribute("loginAccount", adminService.getAdminByEmail(info[1]));
                        session.setAttribute("isAdmin", true);
                        logger.debug("本地Cookie登录，管理员");
                    }
                } else {
                    //普通用户信息，验证密码
                    if (userService.loginIdentify(info[1], info[2])) {
                        session.setAttribute("loginAccount", userService.getUserByEmail(info[1]));
                        logger.debug("本地Cookie登录，用户");
                    }
                }
            }
        }
        logger.debug("无本地Cookie");
        return true;
    }
}