package com.shj.eids.service;

import com.shj.eids.dao.AdminMapper;
import com.shj.eids.dao.EpidemicMsgMapper;
import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.User;
import com.shj.eids.exception.UserLevelException;
import com.sun.mail.imap.protocol.INTERNALDATE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EpidemicMsgService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-16 21:56
 **/
@Service
public class EpidemicMsgService {
    @Autowired
    private EpidemicMsgMapper epidemicMsgMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminMapper adminMapper;

    public void publishArticle(@NonNull User u, @NonNull String title, String content) throws UserLevelException {
        if(u.getLevel() <= 1){
            throw new UserLevelException("没有权限");
        }
        EpidemicMsg msg = new EpidemicMsg(null, title, content, new Date(), 1, u, 0);
        epidemicMsgMapper.addEpidemicMsg(msg);
    }
    /*
     * @Title: getArticles
     * @Description: 对epidemicMsgMapper的封装
     * @param id: 防疫资讯的id
     * @param context: 模糊匹配防疫资讯的内容
     * @param authorId: 作者id
     * @param weight: 资讯的权重
     * @param start: 分页用的开始位置
     * @param length: 分页用的长度
     * @param order: 0表示按照发布日期排序    1按照阅读量排序    2表示获取摘要内容
     * @return java.util.List<com.shj.eids.domain.EpidemicMsg>
     * @Author: ShangJin
     * @Date: 2020/3/18
     */
    public List<EpidemicMsg> getArticles(Integer id, String content, Integer authorId, Integer weight, Integer start, Integer length, @NonNull Integer order){
        Map<String, Object> args = new HashMap<>();
        args.put("id", id);
        args.put("content", content);
        args.put("authorId", authorId);
        args.put("weight", weight);
        args.put("start", start);
        args.put("length", length);
        List<EpidemicMsg> res;
        switch (order){
            case 0:  res = epidemicMsgMapper.getEpidemicMsg(args); break;
            case 1: res= epidemicMsgMapper.getEpidemicMsgOrderByViews(args);break;
            case 2: res = epidemicMsgMapper.getEpidemicAbstract(args);break;
            default:
                throw new IllegalStateException("Unexpected value: " + order);
        }
        return res;
    }
    public EpidemicMsg getArticleById(Integer id){
        List<EpidemicMsg> articles = getArticles(id, null, null, null, null, null,0);
        if(articles.size() == 0){
            return null;
        }
        return articles.get(0);
    }

    public List<EpidemicMsg> getArticles(Integer start, Integer length){
        return getArticles(null, null, null, null, start, length, 0);
    }

    public List<EpidemicMsg> getArticlesByViewOrder(Integer start, Integer length){
        return getArticles(null, null, null, null, start, length, 1);
    }
    public List<EpidemicMsg> getArticlesAbstract(Integer start, Integer length){
        return getArticles(null, null, null, null, start, length, 2);
    }

    public Integer getCount(String content, Integer weight, Integer authorId){
        return epidemicMsgMapper.getCount(content, weight, authorId);
    }

    public void updateEpidemicMsg(EpidemicMsg msg){
        epidemicMsgMapper.updateEpidemicMsg(msg);
    }
}