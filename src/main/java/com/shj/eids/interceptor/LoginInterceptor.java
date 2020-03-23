package com.shj.eids.interceptor;

import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: LoginInterceptor
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 20:14
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object u = session.getAttribute("loginAccount");
        logger.debug("登录拦截器执行");
        if(u != null){
            //用户已登录
            logger.debug("用户已登录，放行");
            return true;
        }
        logger.debug("未登录,拦截,请求转发至登录界面");
        request.setAttribute("msg", "本页面需要登录才能访问");
        request.getRequestDispatcher("/login").forward(request, response);
        return false;
    }
}