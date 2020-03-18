package com.shj.eids.controller;

import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.User;
import com.shj.eids.service.EpidemicMsgService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @ClassName: ArticleController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-16 20:55
 **/
@Controller
public class ArticleController {
    @Autowired
    private EpidemicMsgService msgService;

    @RequestMapping("/user/article")
    public String toArticlePage(Model model){
        List<EpidemicMsg> list = msgService.getArticlesByViewOrder(0, 10);
        model.addAttribute("hotArticles",list);
        return "articles";
    }

    @RequestMapping("/user/article/edit")
    public String toPublish(HttpSession session){
        User u = (User) session.getAttribute("loginAccount");
        if(u == null || u.getLevel() < 2){
            return "redirect:/index";
        }else{
            return "publish";
        }
    }

    @RequestMapping("/user/article/display/{id}")
    public String showArticle(@PathVariable("id") Integer id,
                              Model model){
        EpidemicMsg article = msgService.getArticleById(id);
        article.setViews(article.getViews() + 1);
        msgService.updateEpidemicMsg(article);
        model.addAttribute("article", article);
        return "show";
    }
}