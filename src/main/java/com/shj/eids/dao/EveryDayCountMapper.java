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
     * @Title: getIntervalCount
     * @Description:获取一段时间内，每天满足status要求的患者数量，以每天为单位，List<Integer>形式返回
     * @param args: Map<String, Object>类型, key可以取值：
     *              epidemicEventId: 疫情事件的id
     *              province: 患者所在的省
     *              startDate: 要查询的开始时间日期
     *              endDate: 要查询的截止时间
     *              status:List<String> 类型，表示要统计的患者的状态
     * @return java.util.List<java.lang.Integer>
     * @Author: ShangJin
     * @Date: 2020/3/13
     */
    public List<Integer> getIntervalCount(Map<String, Object>args);
}
