package com.shj.eids.interceptor;

import com.shj.eids.domain.User;
import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Autowired
    AdminService adminService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("loginAccount");
        if(u != null){
            //用户已登录
            return true;
        }
        else{
            //用户未登录
            Cookie []cookies = request.getCookies();
            String info[] = null;
            for(Cookie cookie: cookies){
                if(cookie.getName().equals("login")){
                    info = cookie.getValue().split("-");
                    break;
                }
            }
            if(info != null){
                //本地存有登录信息
                if("admin".equals(info[0])){
                    //管理员登录信息，验证密码
                    if(adminService.loginIdentify(info[1], info[2])){
                        session.setAttribute("loginAccount", adminService.getAdminByEmail(info[1]));
                        return true;
                    }
                }
                else {
                    //普通用户信息，验证密码
                    if(userService.loginIdentify(info[1], info[2])){
                        session.setAttribute("loginAccount", userService.getUserByEmail(info[1]));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}