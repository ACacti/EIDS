package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.service.AdminService;
import com.shj.eids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login/m-identify")
    public String loginIdentify(@RequestParam("email") String email,
                                @RequestParam("password") String password,
                                @RequestParam("isAmdin") boolean isAdmin){
        boolean result;
        if(isAdmin){
            result = adminService.loginIdentify(email, password);
        }else {
            result = userService.loginIdentify(email, password);
        }
        Map<String, Object> res = new HashMap<>();
        if(result){
            res.put("identification ", true);
        }else{
            res.put("identification", false);
            res.put("msg", "邮箱未注册或密码错误");
        }
        return JSON.toJSONString(res);
    }
}