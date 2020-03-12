package com.shj.eids.dao;

import com.shj.eids.domain.EveryDayCount;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.spi.LocaleServiceProvider;

/**
 * @ClassName: EveryDayCountMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-12 22:48
 **/
@Mapper
@Repository
public interface EveryDayCountMapper {
    public void addEveryDayCount(EveryDayCount count);
    /*
     * @Title: getCounts
     * @Description: 获取对应条件的确诊患者数目
     * @param args: Map<String, Object>类型, key可以取值：
     *              epidemicEventId: 疫情事件的id
     *              province: 患者所在的省
     *              date: 要查询的日期
     *              status:List<String> 类型，表示要统计的患者的状态
     * @return java.util.List<java.lang.Integer>
     * @Author: ShangJin
     * @Date: 2020/3/12
     */
    public List<Integer> getCounts(Map<String, Object>args);
}
