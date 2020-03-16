package com.shj.eids.dao;

import com.shj.eids.domain.EpidemicMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EpidemicMsg
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 12:55
 **/
@Repository
@Mapper
public interface EpidemicMsgMapper {
    public void addEpidemicMsg(EpidemicMsg msg);
    public void deleteEpidemicMsg(EpidemicMsg msg);
    public void updateEpidemicMsg(EpidemicMsg msg);
    /*
     * @Title: getEpidemicMsg
     * @Description:
     * @param args: args的key可以的取值：
     *              id: 要查询资讯的id
     *              content:要查询资讯所包含的内容（模糊查询)
     *              authorId: 要查询资讯的作者id
     *              weight: 文章的权重
     *              start和length：分页的起始位置和长度
     *              返回结果按权重和发布日期排序
     * @return java.util.List<com.shj.eids.domain.EpidemicMsg>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<EpidemicMsg> getEpidemicMsg(Map<String, Object> args);
    /*
     * @Title: getEpidemicMsgOrderByViews
     * @Description:
     * @param args: args的key可以的取值：
     *              id: 要查询资讯的id
     *              content:要查询资讯所包含的内容（模糊查询)
     *              authorId: 要查询资讯的作者id
     *              weight: 文章的权重
     *              start和length：分页的起始位置和长度
     *              返回结果按阅读量排序
     * @return java.util.List<com.shj.eids.domain.EpidemicMsg>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<EpidemicMsg> getEpidemicMsgOrderByViews(Map<String, Object> args);
}
