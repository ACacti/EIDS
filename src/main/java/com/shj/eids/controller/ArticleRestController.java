package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.User;
import com.shj.eids.exception.UserLevelException;
import com.shj.eids.service.EpidemicMsgService;
import com.shj.eids.utils.TransferImageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArticleRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-18 21:32
 **/
@RestController
public class ArticleRestController {
    @Autowired
    private EpidemicMsgService msgService;

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

    @PostMapping("/user/articles/{page}")
    public String getArticle(@PathVariable("page") Integer page){
        Map<String, Object> res = new HashMap<>();
        try {
            Integer num = msgService.getCount(null, null, null);
            Integer pageNum = num % 8 == 0 ? num / 8 : num / 8 + 1;//总页数
            res.put("pages", pageNum);
            List<EpidemicMsg> list = msgService.getArticlesAbstract((page - 1) * 8, 8);
            res.put("data",list);
            res.put("msg", "success");
            return JSON.toJSONString(res,SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }
    }
}