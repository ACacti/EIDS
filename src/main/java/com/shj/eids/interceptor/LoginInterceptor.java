package com.shj.eids.interceptor;

import com.shj.eids.domain.User;
import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
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
        User u = (User) session.getAttribute("loginAccount");
        logger.debug("登录拦截器执行");
        if(u != null){
            //用户已登录
            logger.debug("用户已登录，放行");
            return true;
        }
        else{
            //用户未登录
            Cookie []cookies = request.getCookies();
            String[] info = null;
            logger.debug("查询本地Cookies");
            for(Cookie cookie: cookies){
                logger.debug(cookie.getName() + ":" + cookie.getValue());
                if(cookie.getName().equals("login")){
                    info = cookie.getValue().split("-");
                    logger.debug("本地Cookie:" + info[0] + "," + info[1] + "," + info[2]);
                    break;
                }
            }
            if(info != null){
                //本地存有登录信息
                if("admin".equals(info[0])){
                    //管理员登录信息，验证密码
                    if(adminService.loginIdentify(info[1], info[2])){
                        session.setAttribute("loginAccount", adminService.getAdminByEmail(info[1]));
                        logger.debug("本地Cookie登录，管理员");
                        return true;
                    }
                }
                else {
                    //普通用户信息，验证密码
                    logger.debug("userService==null?" + (userService==null));
                    logger.debug("adminService==null?" + (adminService==null));
                    if(userService.loginIdentify(info[1], info[2])){
                        session.setAttribute("loginAccount", userService.getUserByEmail(info[1]));
                        logger.debug("本地Cookie登录，用户");
                        return true;
                    }
                }
            }
        }
        logger.debug("未登录且本地无Cookie,拦截");
        request.getRequestDispatcher("/login").forward(request, response);
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}