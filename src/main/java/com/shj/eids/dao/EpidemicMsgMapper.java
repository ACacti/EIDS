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
    /*
     * @Title: deleteEpidemicMsg
     * @Description: 按作者Id或者文章id删除文章
     * @param args: key的类型可以为：
     *              id: 要删除的文章id
     *              authorId: 要删除的作者id
     * @return void
     * @Author: ShangJin
     * @Date: 2020/4/7
     */
    public void deleteEpidemicMsg(Map<String, Object> args);
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

    /*
     * @Title: getEpidemicMsgOrderByViews
     * @Description:获取简要版的防疫资讯，content长度固定为最多250个字符
     * @param args: args的key可以的取值：
     *              id: 要查询资讯的id
     *              content:要查询资讯所包含的内容（模糊查询)
     *              authorId: 要查询资讯的作者id
     *              weight: 文章的权重
     *              abstract: 获取的content的长度
     *              start和length：分页的起始位置和长度
     *              返回结果按阅读量排序, 并只显示
     * @return java.util.List<com.shj.eids.domain.EpidemicMsg>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<EpidemicMsg> getEpidemicAbstract(Map<String, Object> args);

    /*
     * @Title: getCount
     * @Description: 获取符合条件的资讯的数目
     * @param content: 模糊查询资讯的内容
	 * @param weight: 资讯的权重
	 * @param authorId: 资讯的作者
     * @return java.lang.Integer
     * @Author: ShangJin
     * @Date: 2020/3/18
     */
    public Integer getCount(@Param("content") String content,
                            @Param("weight") Integer weight,
                            @Param("authorId") Integer authorId);
}
