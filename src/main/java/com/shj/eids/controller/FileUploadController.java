package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.User;
import com.shj.eids.utils.TransferImageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: FileUploadController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-30 15:12
 **/
@RestController
public class FileUploadController {
    /*
     * 处理富文本编辑器上传图片
     * 根据session中登录的用户判断上传的图片是源自 [防疫资讯] 还是[援助请求]
     *      如果登录的账号是普通用户，则将图片存储在 [防疫资讯]图片的文件夹下
     *      如果登录的账号是管理员，则将图片存储在[援助请求]的图片文件夹下
     */
    @PostMapping("/user/img/doUpload")
    public String imgUpload(@RequestParam("edit") MultipartFile file,
                            HttpServletRequest request){
        Map<String, Object> res = new HashMap<>();
        String realPath = getClass().getResource("/").getPath();
        String contextPath = request.getContextPath();
        HttpSession session = request.getSession();
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        Integer id;
        try {
            String imgLocation = null;
            if(isAdmin == null || !isAdmin){
                //登录的账号为普通用户
                User user = (User) session.getAttribute("loginAccount");
                if(user == null){
                    //未登录
                    return "error";
                }
                imgLocation = TransferImageUtil.transferImage(file, realPath,"user/", contextPath, user.getId().toString());
            }else{
                //登录的账号为管理员
                Admin admin = (Admin) session.getAttribute("loginAccount");
                if(admin == null){
                    return "error";
                }
                imgLocation = TransferImageUtil.transferImage(file, realPath, "uploadImage/admin/",contextPath, admin.getId().toString());
            }

            res.put("code", 0);
            Map<String, Object> data = new HashMap<>();
            data.put("location", imgLocation);
            res.put("data", data);
            return JSON.toJSONString(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 1);
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }


}