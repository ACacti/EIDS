package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.User;
import com.shj.eids.exception.UserLevelException;
import com.shj.eids.service.EpidemicMsgService;
import com.shj.eids.utils.TransferImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ArticleController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-16 20:55
 **/
@RestController
public class ArticleController {
    @Autowired
    private EpidemicMsgService msgService;
    @PostMapping("/user/img/doUpload")
    public String imgUpload(@RequestParam("edit") MultipartFile file,
                            HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
//        String realPath =request.getServletContext().getRealPath("/");//未部署的情况下获取到的路径异常
        String realPath = getClass().getResource("/").getPath();
//        String realPath = "C:\\Users\\ShangJin\\Desktop\\WorkSpace\\EpidemicInformationDisseminationSystem\\EIDS\\";
        String contextPath = request.getContextPath();
        User user = (User) request.getSession().getAttribute("loginAccount");
        if(user == null){
            return "error";
        }
        String imgLocation = null;
        try {
            imgLocation = TransferImageUtil.transferImage(file, realPath, contextPath, user.getId().toString());
            map.put("location", imgLocation);
            return JSON.toJSONString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/user/article/publish")
    public String addArticle(@RequestParam("article") String article,
                             @RequestParam("title") String title,
                             HttpSession session){
        User u = (User) session.getAttribute("loginAccount");
        Map<String, Object> res = new HashMap<>();
        if(u == null){
            res.put("msg", "用户未登录");
            return JSON.toJSONString(res);
        }
        try {
            msgService.publishArticle(u, title, article);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        } catch (UserLevelException e) {
            res.put("msg", e.getMessage());
            e.printStackTrace();
            return JSON.toJSONString(res);
        }
    }
}