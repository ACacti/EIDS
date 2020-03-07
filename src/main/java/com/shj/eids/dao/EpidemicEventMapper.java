package com.shj.eids.dao;

import com.shj.eids.domain.EpidemicEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EpidemicEventMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-07 14:55
 **/
@Repository
@Mapper
public interface EpidemicEventMapper {
    public List<EpidemicEvent> getEpidemicEvents(Map<String, Object> args);
    public List<EpidemicEvent> getEpidemicEventsByPublisherId(@Param("id") Integer id, @Param("start") Integer start, @Param("length") Integer length);
}
