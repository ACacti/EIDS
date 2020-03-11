package com.shj.eids.dao;

import com.shj.eids.domain.EpidemicEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EpidemicEventMapper.xml
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-07 14:55
 **/
@Repository
@Mapper
public interface EpidemicEventMapper {
    public void addEpidemicEvent(EpidemicEvent event);
    public void deleteEpidemicEventById(Integer id);
    /*
     * @Title: getEpidemicEvents
     * @Description:
     * @param args:Map类型，key可取的值为
     *              id: 疫情事件的id
     *              name: 所查询疫情事件的名字
     *              publisherId: 疫情事件发布者的Id
     * @return java.util.List<com.shj.eids.domain.EpidemicEvent>
     * @Author: ShangJin
     * @Date: 2020/3/10
     */
    public List<EpidemicEvent> getEpidemicEvents(Map<String, Object> args);
    public List<EpidemicEvent> getEpidemicEventsByPublisherId(@Param("id") Integer id, @Param("start") Integer start, @Param("length") Integer length);
}
