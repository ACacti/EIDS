package com.shj.eids.dao;

import com.shj.eids.domain.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BrowseHistoryMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 12:11
 **/
@Mapper
@Repository
public interface BrowseHistoryMapper {
    public void addHistory(BrowseHistory history);
    public void deleteHistory(BrowseHistory history);
    /*
     * @Title: getHistories
     * @Description:
     * @param map:
     *          map 的key可以为
     *                 "userId":表示查询某用户的浏览记录
     *                 "start"和"length":SQL语句limit的两个参数，用于查询结果分页
     *                  查询结果按浏览时间正序排序
     *              此外key可以为start和length，表示SQL语句limit的参数
     * @return java.util.List<com.shj.eids.domain.BrowseHistory>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<BrowseHistory> getHistories(Map<String, Object> map);
}
