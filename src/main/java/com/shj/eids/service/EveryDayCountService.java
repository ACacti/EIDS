package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.dao.EveryDayCountMapper;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.domain.EveryDayCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EveryDayCountService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-13 16:45
 **/
@Service
public class EveryDayCountService {
    private final EveryDayCountMapper everyDayCountMapper;
    private final EpidemicEventMapper eventMapper;

    private final EpidemicInfoService epidemicInfoService;

    String []status = EpidemicInfoService.status;
    @Autowired
    public EveryDayCountService(EveryDayCountMapper everyDayCountMapper, EpidemicEventMapper eventMapper, EpidemicInfoService epidemicInfoService) {
        this.everyDayCountMapper = everyDayCountMapper;
        this.eventMapper = eventMapper;
        this.epidemicInfoService = epidemicInfoService;
    }

    @Scheduled(cron="0 59 23 * * ?") //每天的23:59执行此任务
    public void makeEveryDayCount(){
        // 各疫情事件 各省 四种状态 数量
        Map<String, Object> map = new HashMap<>();
        //空参，表示查询全部
        List<EpidemicEvent>  events = eventMapper.getEpidemicEvents(map);
        String []provinces = EpidemicInfoService.provinces;
        Date now = new Date();
        for(EpidemicEvent event: events){
            for(String province: provinces){
                for(String s: status){
                    Integer num = epidemicInfoService.getPatientCountByStatus(event.getId(), province, null, s);
                    EveryDayCount count = new EveryDayCount(null, event.getId(), province, now, s, num);
                    everyDayCountMapper.addEveryDayCount(count);
                }
            }
        }
    }

    /*
     * @Title: makeEveryDayCount
     * @Description: 此方法仅在模拟添加数据需要指定当时时间的时候使用
     * @param date:
     * @return void
     * @Author: ShangJin
     * @Date: 2020/3/13
     */
    public void makeEveryDayCount(Date date){
        // 各疫情事件 各省 四种状态 数量
        Map<String, Object> map = new HashMap<>();
        //空参，表示查询全部
        List<EpidemicEvent>  events = eventMapper.getEpidemicEvents(map);
        String []provinces = EpidemicInfoService.provinces;
        Date now = date;
        for(EpidemicEvent event: events){
            for(String province: provinces){
                for(String s: status){
                    Integer num = epidemicInfoService.getPatientCountByStatus(event.getId(), province, null, s);
                    EveryDayCount count = new EveryDayCount(null, event.getId(), province, now, s, num);
                    everyDayCountMapper.addEveryDayCount(count);
                }
            }
        }
    }
}